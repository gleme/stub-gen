JFLAGS = -cp "RPCGen.jar:antlr/antlr-4.5.3-complete.jar:."
ANTLR4 = java $(JFLAGS) -jar antlr/antlr-4.5.3-complete.jar

G4DIR = src/generator/unifei/eco009/antlr4
GENDIR = src/generator/unifei/eco009/generator
APPDIR = src/application/unifei/eco009/application
STUBSDIR = src/application/unifei/eco009/stubs
SERVERDIR = src/application/unifei/eco009/server

all: jargen
	@cat res/stub.sh RPCGen.jar > RPCGen && chmod +x RPCGen

grammar:
	@$(ANTLR4) $(G4DIR)/RPCGen.g4

generator: grammar
	@javac $(JFLAGS) $(G4DIR)/*.java $(GENDIR)/*.java

jargen: generator
	@jar -cmf res/MANIFEST.MF RPCGen.jar -C src/ .	

app:
	@javac $(STUBSDIR)/*.java $(SERVERDIR)/*.java $(APPDIR)/*.java

jarapp: app
	@jar -cmf res/server/MANIFEST.MF Server.jar -C src/ .
	@jar -cmf res/app/MANIFEST.MF App.jar -C src/ .

clean:
	@echo "Cleaning unnecessary files..."
	@rm -rf $(GENDIR)/*.class $(G4DIR)/*.class $(G4DIR)/*.tokens $(G4DIR)/*.java *.jar *.class *.tokens *.java RPCGen
	@rm -rf $(APPDIR)/*.class $(STUBSDIR)/*.class $(SERVERDIR)/*.class App

install: all
	@echo "export CLASSPATH='.:/usr/local/lib/antlr-4.5.3-complete.jar:$CLASSPATH'" >> ~/.bashrc