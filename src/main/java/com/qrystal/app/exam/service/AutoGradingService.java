package com.qrystal.app.exam.service;

import com.qrystal.app.exam.mapper.ExamAnswerMapper;
import com.qrystal.app.exam.mapper.ExamMapper;
import com.qrystal.app.exam.model.ExamAnswer;
import com.qrystal.app.exam.model.ExamQuestion;
import com.qrystal.app.question.model.Question;
import com.qrystal.app.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoGradingService {
    private final QuestionService questionService;
    private final ExamAnswerMapper examAnswerMapper;
    private final ExamMapper examMapper; // ExamMapper 추가

    public int gradeExamAttempt(Long examId, List<ExamAnswer> answers) {
        int totalScore = 0;

        for (ExamAnswer answer : answers) {
            Question question = questionService.getQuestion(answer.getQuestionId());
            // exam_questions 테이블에서 해당 문제의 배점 조회
            ExamQuestion examQuestion = examMapper.findQuestionByExamQuestionId(
                    examId,
                    answer.getQuestionId()
            );

            switch(question.getTypeId().intValue()) {
                case 1: // 객관식
                    totalScore += gradeMultipleChoice(answer, question, examQuestion.getPoint());
                    break;

                case 2: // 주관식
                    totalScore += gradeShortAnswer(answer, question, examQuestion.getPoint());
                    break;

                case 3: // 서술형
                    markForManualGrading(answer);
                    break;

                default:
                    log.warn("Unknown question type: {}", question.getTypeId());
                    markForManualGrading(answer);
            }
        }

        return totalScore;
    }

    private int gradeMultipleChoice(ExamAnswer answer, Question question, int point) {
        boolean isCorrect = question.getAnswer().trim()
                .equalsIgnoreCase(answer.getSubmittedAnswer().trim());

        answer.setIsCorrect(isCorrect);
        answer.setScore(isCorrect ? point : 0);
        answer.setIsGraded(true);

        examAnswerMapper.updateGrading(
                answer.getId(),
                answer.getIsCorrect(),
                answer.getScore(),
                null
        );

        return answer.getScore();
    }

    private int gradeShortAnswer(ExamAnswer answer, Question question, int point) {
        boolean isCorrect = question.getAnswer().trim()
                .equalsIgnoreCase(answer.getSubmittedAnswer().trim());

        answer.setIsCorrect(isCorrect);
        answer.setScore(isCorrect ? point : 0);
        answer.setIsGraded(true);

        examAnswerMapper.updateGrading(
                answer.getId(),
                answer.getIsCorrect(),
                answer.getScore(),
                null
        );

        return answer.getScore();
    }

    private void markForManualGrading(ExamAnswer answer) {
        answer.setIsCorrect(null);
        answer.setScore(null);
        answer.setIsGraded(false);
        answer.setGradingComment("서술형 문제는 수동 채점이 필요합니다.");

        examAnswerMapper.updateGrading(
                answer.getId(),
                null,
                null,
                answer.getGradingComment()
        );
    }
}