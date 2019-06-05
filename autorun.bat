@echo on
　　　set /a N=1
　　　:LOOP
　　　
　　　　java -jar Maze_Lsystem.jar
　　　
　　　if "%N%"=="100" (goto EXIT)
　　　set /a N=N+1
　　　goto LOOP
　　　:EXIT