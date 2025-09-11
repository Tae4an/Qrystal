-- 컬럼 추가 (예외 상황 처리)
-- MySQL 5.7 이하 버전에서는 IF NOT EXISTS 지원하지 않음
ALTER TABLE exam_attempts ADD COLUMN question_order TEXT;