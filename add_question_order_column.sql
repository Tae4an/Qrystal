-- 문제 순서 필드를 추가하는 마이그레이션 스크립트
ALTER TABLE exam_attempts ADD COLUMN question_order TEXT;
