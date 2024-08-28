
:; echo "shell hello"
:; echo "stop-ds.bat: Running as shell script"
:; STOP_DS="$1/bin/stop-ds"
:; echo "stop-ds.bat: STOP_DS is $STOP_DS"
:;
:; if [ -x "$STOP_DS" ]; then
:;   echo "running $STOP_DS"
:;   $STOP_DS
:; fi
:;
:; exit 0

@echo off
echo Windows hello
echo stop-ds.bat Running as cmd script
set arg=%1
set STOP_DS_DIR=%arg%/bat
echo stop-ds.bat: STOP_DS_DIR is %STOP_DS_DIR%
cd %STOP_DS_DIR%
if exist stop-ds.bat (
stop-ds.bat
)
exit 0
