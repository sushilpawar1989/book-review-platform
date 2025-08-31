#!/bin/bash

echo "🧪 Testing Local Builds"
echo "======================"

# Test Backend Build
echo "🏗️ Testing Backend Build..."
cd backend
chmod +x gradlew
./gradlew clean build
if [ $? -eq 0 ]; then
    echo "✅ Backend build successful!"
    echo "📦 JAR created: $(ls build/libs/*.jar)"
else
    echo "❌ Backend build failed!"
    exit 1
fi
cd ..

# Test Frontend Build
echo "⚛️ Testing Frontend Build..."
cd frontend
npm ci
npm run build
if [ $? -eq 0 ]; then
    echo "✅ Frontend build successful!"
    echo "📦 Build created: $(ls -la dist/)"
else
    echo "❌ Frontend build failed!"
    exit 1
fi
cd ..

echo ""
echo "🎉 All builds completed successfully!"
echo ""
echo "📊 Build Summary:"
echo "  Backend JAR: backend/build/libs/"
echo "  Frontend Dist: frontend/dist/"
