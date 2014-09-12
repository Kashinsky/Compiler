JC = javac
CLASSPATH = ../..
JFLAGS = -classpath $(CLASSPATH)
SOURCES = Lazy.java LineIO.java LineIOTest.java StreamReader.java 
CLASSES = $(SOURCES:.java=.class)

all:	$(CLASSES)

%.class: 	%.java
	$(JC) $(JFLAGS) $<
	chmod a+r $@

clean:
	rm *.class
