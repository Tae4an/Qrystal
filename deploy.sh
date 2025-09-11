#!/bin/bash

# AWS 프리티어 배포 스크립트
# 사용법: ./deploy.sh

set -e

echo "🚀 Qrystal AWS 프리티어 배포 시작..."

# 환경 변수 확인
if [ -z "$DB_HOST" ] || [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ] || [ -z "$JASYPT_PASSWORD" ]; then
    echo "❌ 환경 변수가 설정되지 않았습니다!"
    echo "다음 변수들을 설정해주세요:"
    echo "export DB_HOST='<RDS 엔드포인트>'"
    echo "export DB_USERNAME='<데이터베이스 사용자명>'"
    echo "export DB_PASSWORD='<데이터베이스 비밀번호>'"
    echo "export JASYPT_PASSWORD='<암호화 키>'"
    echo ""
    echo "💡 자세한 설정 방법은 env.example 파일을 참조하세요!"
    echo "   cp env.example .env && nano .env"
    exit 1
fi

# 기존 컨테이너 중지 및 제거
echo "📦 기존 컨테이너 정리 중..."
docker-compose -f docker-compose.prod.yml down || true

# 최신 이미지 빌드
echo "🔨 Docker 이미지 빌드 중..."
docker-compose -f docker-compose.prod.yml build --no-cache

# 컨테이너 시작
echo "🎯 서비스 시작 중..."
docker-compose -f docker-compose.prod.yml up -d

# 헬스체크
echo "🏥 헬스체크 대기 중..."
sleep 30

# 포트 설정 (환경변수 또는 기본값 80)
PORT=${APP_PORT:-80}

if curl -f http://localhost:$PORT/actuator/health > /dev/null 2>&1; then
    echo "✅ 배포 완료! 서비스가 정상 실행 중입니다."
    
    # AWS EC2에서 실행 중인지 확인
    PUBLIC_IP=$(curl -s --connect-timeout 2 http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null)
    
    if [ -n "$PUBLIC_IP" ] && [ "$PUBLIC_IP" != "localhost" ]; then
        # AWS EC2 환경
        if [ "$PORT" = "80" ]; then
            echo "🌍 접속 URL: http://$PUBLIC_IP"
        else
            echo "🌍 접속 URL: http://$PUBLIC_IP:$PORT"
        fi
        echo "📍 AWS EC2 환경에서 실행 중"
    else
        # 로컬 또는 다른 환경
        if [ "$PORT" = "80" ]; then
            echo "🏠 로컬 접속 URL: http://localhost"
        else
            echo "🏠 로컬 접속 URL: http://localhost:$PORT"
        fi
        echo "💻 로컬 환경에서 실행 중"
    fi
else
    echo "❌ 배포 실패! 로그를 확인해주세요."
    docker-compose -f docker-compose.prod.yml logs
    exit 1
fi

# 컨테이너 상태 확인
echo "📊 컨테이너 상태:"
docker-compose -f docker-compose.prod.yml ps

echo "🎉 배포 완료!"
