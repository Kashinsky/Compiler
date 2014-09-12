JARFILE = 463.jar
DIRS = fsm util
EXTRADIRS = gnu
DIR = cis463
.PHONY: dirs $(DIRS) clean all

all:	../$(JARFILE)

dirs:	$(DIRS)

$(DIRS):
	cd $@ ; make

../$(JARFILE):	dirs
	chmod a+r */*.class; \
	cd .. ; \
	jar cf $(JARFILE) $(DIR) $(EXTRADIRS); \
	chmod a+r $(JARFILE)

clean:
	for dir in $(DIRS); do \
		(cd $$dir ; make clean); \
	done
