#!/bin/bash

echo "=== تعليمات تشغيل التطبيق ==="
echo ""
echo "1. إيقاف وحذف الـ containers القديمة:"
echo "   docker-compose down"
echo ""
echo "2. بناء وتشغيل التطبيق من جديد:"
echo "   docker-compose up -d postgres"
echo "   sleep 10"
echo "   docker-compose up -d spring-app"
echo ""
echo "3. مشاهدة الـ logs:"
echo "   docker logs spring-app -f"
echo ""
echo "4. اختبار Register في Postman:"
echo "   POST http://localhost:9090/api/auth/register"
echo "   Body (JSON):"
echo '   {
     "email": "user@example.com",
     "password": "password123",
     "name": "Test User",
     "role": "CLIENT"
   }'
echo ""
echo "5. اختبار Login في Postman:"
echo "   POST http://localhost:9090/api/auth/login"
echo "   Body (JSON):"
echo '   {
     "email": "user@example.com",
     "password": "password123"
   }'
echo ""
echo "=== التغييرات التي تمت ==="
echo "✅ 1. تم إصلاح RefreshToken entity (LAZY -> EAGER)"
echo "✅ 2. تم إضافة @Transactional للـ login method"
echo "✅ 3. تم إصلاح logback-spring.xml (إضافة console appender)"
echo "✅ 4. تم تغيير JWT_SECRET إلى قيمة أطول (256 bits)"
echo ""
echo "الـ Application يعمل على: http://localhost:9090"
echo "Swagger UI: http://localhost:9090/swagger-ui.html"

