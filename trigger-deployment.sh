#!/bin/bash

echo "🚀 Trigger GitHub Actions Deployment"
echo "===================================="

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "❌ Not in a git repository"
    exit 1
fi

# Check if we have uncommitted changes
if ! git diff-index --quiet HEAD --; then
    echo "⚠️  You have uncommitted changes. Please commit them first."
    echo ""
    echo "📋 Uncommitted files:"
    git status --porcelain
    echo ""
    read -p "🤔 Do you want to commit all changes now? (y/N): " COMMIT_NOW
    
    if [[ $COMMIT_NOW =~ ^[Yy]$ ]]; then
        git add .
        read -p "📝 Enter commit message: " COMMIT_MSG
        if [ -z "$COMMIT_MSG" ]; then
            COMMIT_MSG="deploy: trigger AWS deployment"
        fi
        git commit -m "$COMMIT_MSG"
        echo "✅ Changes committed"
    else
        echo "❌ Please commit your changes first"
        exit 1
    fi
fi

# Get current branch
CURRENT_BRANCH=$(git branch --show-current)
echo "📍 Current branch: $CURRENT_BRANCH"

# Check if we're on main branch
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "⚠️  You're not on the main branch"
    read -p "🤔 Do you want to switch to main and merge your changes? (y/N): " SWITCH_MAIN
    
    if [[ $SWITCH_MAIN =~ ^[Yy]$ ]]; then
        git checkout main
        git pull origin main
        git merge $CURRENT_BRANCH
        echo "✅ Merged $CURRENT_BRANCH into main"
    else
        echo "ℹ️  Deployment will trigger from current branch: $CURRENT_BRANCH"
    fi
fi

# Push to trigger deployment
echo ""
echo "🚀 Pushing to trigger GitHub Actions deployment..."
git push origin $(git branch --show-current)

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Push successful! Deployment triggered."
    echo ""
    echo "📊 Monitor deployment progress:"
    echo "🔗 https://github.com/sushilpawar1989/book-review-platform/actions"
    echo ""
    echo "📋 Deployment will:"
    echo "  1. 🏗️  Build Docker images for backend and frontend"
    echo "  2. 📦 Push images to AWS ECR"
    echo "  3. 🏗️  Deploy infrastructure with Terraform"
    echo "  4. 🚀 Update ECS service with new images"
    echo "  5. 🔍 Verify deployment health"
    echo ""
    echo "⏱️  Expected deployment time: 10-15 minutes"
    echo ""
    echo "🎯 Next steps:"
    echo "  - Watch the GitHub Actions tab for progress"
    echo "  - Check AWS ECS console for service status"
    echo "  - Test the deployed application once complete"
else
    echo "❌ Push failed. Please check your git configuration."
    exit 1
fi
