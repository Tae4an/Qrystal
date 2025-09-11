-- IF NOT EXISTS 구문 없이 컬럼 추가
-- 컬럼이 이미 존재하는지 먼저 확인
SELECT COUNT(*) INTO @exists FROM information_schema.columns 
WHERE table_schema = 'qrystal' AND table_name = 'exam_attempts' AND column_name = 'question_order';

-- 없으면 추가
SET @query = IF(@exists = 0, 'ALTER TABLE exam_attempts ADD COLUMN question_order TEXT', 'SELECT "Column already exists"');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;