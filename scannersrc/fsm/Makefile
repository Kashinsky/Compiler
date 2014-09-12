JC = javac
CLASSPATH = ../..
JFLAGS = -classpath $(CLASSPATH)
SOURCES = FSMState.java FSM.java
CLASSES = $(SOURCES:.java=.class)

all:	$(CLASSES)

%.class: 	%.java
	$(JC) $(JFLAGS) $<
	chmod a+r $@

clean:
	rm *.class
