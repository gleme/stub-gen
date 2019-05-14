package generator.unifei.eco009.generator;

import java.io.*;
import java.util.BitSet;

import generator.unifei.eco009.antlr4.RPCGenLexer;
import generator.unifei.eco009.antlr4.RPCGenParser;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class RPCGen {

    public static void main(String[] args) {

        File sourceFile = null;
        String serviceName = "MyService";

        try {
            switch (args.length) {
                case 0:
                    printUsageMessage();
                    System.exit(0);
                    break;

                case 2:
                    serviceName = args[1];

                case 1:
                    sourceFile = new File(args[0]);
                    break;

                default:
                    System.out.println("[ERROR] Invalid number of arguments!");
                    printUsageMessage();
                    System.exit(1);
            }

            if(!sourceFile.exists()) {
                System.out.println("[ERROR] Source file not found.");
                System.exit(1);
            }

            ANTLRInputStream inputFile = new ANTLRInputStream(new FileInputStream(sourceFile));
            RPCGenLexer lexer = new RPCGenLexer(inputFile);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RPCGenParser parser = new RPCGenParser(tokens);

            parser.addErrorListener(new ANTLRErrorListener() {
                @Override public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2, int arg3, String arg4, RecognitionException arg5) {
                    System.out.println("iERROR: (" + arg2 + ", " + arg3 + ") SyntaxError -- " + arg5.getMessage());
                }
                @Override public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {}
                @Override public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {}
                @Override public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5, ATNConfigSet arg6) {}
            });

            serviceName = StubsCreator.capitalizeFirst(serviceName);
            ParseTree tree = parser.program();
            ParseTreeWalker walker = new ParseTreeWalker();
            StubsCreator serverStub = new StubsCreator(serviceName);
            walker.walk(serverStub, tree);

            // generate server class
            File file = new File("gen/server/" + serviceName + ".java");
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(file);
            writer.print(serverStub.serverCode.toString());
            writer.close();

            // generate server information class
            file = new File("gen/stubs/ServerInfo.java");
            file.getParentFile().mkdirs();
            writer = new PrintWriter(file);
            writer.print(serverStub.serverInfoCode.toString());
            writer.close();

            // generate server threads class
            file = new File("gen/server/ClientHandler.java");
            file.getParentFile().mkdirs();
            writer = new PrintWriter(file);
            writer.print(serverStub.serverStub.toString());
            writer.close();

            // generate client stub classes
            int index = 0;
            for (StringBuilder sb : serverStub.clientStubs) {
                String className = StubsCreator.capitalizeFirst(serverStub.methodsNames.get(index));
                file = new File("gen/stubs/" + className + ".java");
                writer = new PrintWriter(file);
                writer.print(sb.toString());
                writer.close();
                index++;
            }

            // generate implementation classes
            index = 0;
            for (StringBuilder sb : serverStub.implementationCodes) {
                String className = StubsCreator.capitalizeFirst(serverStub.methodsNames.get(index));
                file = new File("gen/stubs/" + className + "Implementation.java");
                writer = new PrintWriter(file);
                writer.print(sb.toString());
                writer.close();
                index++;
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
        }
    }

    private static void printUsageMessage() {
        System.out.println("usage: $ rpccreate <sourceFile> <service name>");
    }
}
