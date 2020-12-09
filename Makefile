
#---------------------------------------------------------------
# Using this Makefile
#
#   To compile your java source (and generate documentation)
#
#   make
#
#   To clean up your directory (e.g. before submission)
#
#   make clean
#
#---------------------------------------------------------------

JFLAGS= -cp .

# Recognize files with .class and .java extensions
.SUFFIXES: .class .java

# This is a rule to convert a file with .java extension
# into a file with a .class extension. The macro $< just
# supplies the name of the file (without the extension)
# that invoked this rule.

.java.class:
	javac $(JFLAGS) $<


all:  Channel.class ChatClient.class ClientInfo.class Command.class Connection.class Message.class MsgCommand.class ChatServer.class Nick.class TimerStart.class

clean:
	/bin/rm -f *.class
