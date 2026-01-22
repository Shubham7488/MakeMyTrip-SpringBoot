@echo off
echo.
echo ============================================
echo Starting MakeMyTrip Frontend (Next.js)
echo ============================================
echo.

cd makemytour

if not exist "node_modules" (
    echo Installing dependencies...
    call npm install
)

echo.
echo Starting Next.js development server...
echo Frontend will be available at: http://localhost:3000
echo Backend API: http://localhost:8081
echo.

call npm run dev

pause
