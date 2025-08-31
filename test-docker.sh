#!/bin/bash

echo "🐳 Testing Docker Builds Locally"
echo "================================="

# Test Backend Docker Build
echo "🏗️ Building Backend Docker Image..."
cd backend
docker build -t book-review-backend:test .
if [ $? -eq 0 ]; then
    echo "✅ Backend Docker build successful!"
else
    echo "❌ Backend Docker build failed!"
    exit 1
fi
cd ..

# Test Frontend Docker Build  
echo "⚛️ Building Frontend Docker Image..."
cd frontend
docker build -t book-review-frontend:test .
if [ $? -eq 0 ]; then
    echo "✅ Frontend Docker build successful!"
else
    echo "❌ Frontend Docker build failed!"
    exit 1
fi
cd ..

# Test Docker Compose
echo "🚀 Testing Docker Compose..."
docker-compose config
if [ $? -eq 0 ]; then
    echo "✅ Docker Compose configuration valid!"
else
    echo "❌ Docker Compose configuration invalid!"
    exit 1
fi

echo ""
echo "🎉 All Docker tests passed!"
echo ""
echo "🚀 To run the full stack locally:"
echo "   docker-compose up -d"
echo ""
echo "🔍 To view logs:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 To stop:"
echo "   docker-compose down"
