package generator.unifei.eco009.generator;

import org.antlr.v4.runtime.tree.TerminalNode;
import generator.unifei.eco009.antlr4.RPCGenBaseListener;
import generator.unifei.eco009.antlr4.RPCGenParser;

import java.util.ArrayList;
import java.util.List;

public class StubsCreator extends RPCGenBaseListener {

    public static final int INT_RET = 0, STRING_RET = 1;

    public StringBuilder serverCode;
    public StringBuilder serverInfoCode;
    public StringBuilder serverStub;
    public List<String> methodsNames = new ArrayList<>();
    public List<Integer> methodsReturnTypes = new ArrayList<>();
    public List<StringBuilder> clientStubs = new ArrayList<>();
    public List<StringBuilder> implementationCodes = new ArrayList<>();

    private String serviceName;
    private int functionCounter = 0;

    public StubsCreator(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void enterProgram(RPCGenParser.ProgramContext ctx) {
        // build server main thread code
        buildServerCode();

        // build server stub header
        buildServerStubHeader();

        // build server info class
        buildServerInfoClass();
    }

    @Override
    public void exitProgram(RPCGenParser.ProgramContext ctx) {
        // build server stub footer
        buildServerStubFooter();
    }

    @Override public void enterMethod_int(RPCGenParser.Method_intContext ctx) {
        String functionName = ctx.ID().getText();
        String className = capitalizeFirst(functionName);

        // add to list of methods names
        methodsNames.add(functionName);
        methodsReturnTypes.add(INT_RET);

        // build server stub if body
        buildServerStubBody(className, functionName);

        // build client stub header
        clientStubs.add(buildClientStubHeader(className));

        // build implementation stub
        implementationCodes.add(buildImplementationStub(className, functionName, INT_RET));

        functionCounter++;
    }

    @Override public void exitMethod_int(RPCGenParser.Method_intContext ctx) {
        //.append("}");
    }

    @Override
    public void enterMethod_string(RPCGenParser.Method_stringContext ctx) {
        String functionName = ctx.ID().getText();
        String className = capitalizeFirst(functionName);

        // add to list of methods names
        methodsNames.add(functionName);
        methodsReturnTypes.add(STRING_RET);

        // build server stub if body
        buildServerStubBody(className, functionName);

        // build client stub header
        clientStubs.add(buildClientStubHeader(className));

        // build implementation stub
        implementationCodes.add(buildImplementationStub(className, functionName, STRING_RET));

        functionCounter++;
    }

    @Override public void exitMethod_string(RPCGenParser.Method_stringContext ctx) {
        //.append("}");
    }

    @Override public void exitMethod_args(RPCGenParser.Method_argsContext ctx) {

        StringBuilder clientStub = clientStubs.get(functionCounter - 1);
        int index = 0;

        // declare arguments attributes
        for (TerminalNode tn : ctx.getTokens(RPCGenParser.ID)) {
            String varName = tn.getText();
            String varType = ctx.var_type(index).getText();
            clientStub.append("\tprivate " + varType + " " + varName + ";\n");
            index++;
        }

        // declare return value
        String returnType = (methodsReturnTypes.get(functionCounter - 1) == INT_RET? "int" : "String");
        clientStub.append("\tprivate " + returnType + " returnValue;\n");

        index = 0;

        // create gets and sets for arguments attributes
        for (TerminalNode tn : ctx.getTokens(RPCGenParser.ID)) {
            String varName = tn.getText();
            String varType = ctx.var_type(index).getText();
            String standardName = capitalizeFirst(varName);

            clientStub.append(  "\tpublic " + varType + " get" + standardName + "() {\n" +
                                "\t\treturn " + varName + ";\n" +
                                "\t}\n" +
                                "\tpublic void set" + standardName + "(" + varType + " " + varName + ") {\n" +
                                "\t\tthis." + varName + " = " + varName + ";\n" +
                                "\t}\n"
            );
            index++;
        }

        String functionName = methodsNames.get(functionCounter - 1);
        String className = capitalizeFirst(functionName);

        // create get and set for return value
        clientStub.append(  "\tpublic " + returnType + " getReturnValue() {\n" +
                            "\t\treturn returnValue;\n" +
                            "\t}\n" +
                            "\tpublic void setReturnValue(" + returnType + " returnValue) {\n" +
                            "\t\tthis.returnValue = returnValue;\n" +
                            "\t}\n"
        );

        // create stub method header
        clientStub.append(
                            "\tpublic " + returnType + " " + functionName + "("
        );

        // create stub method arguments
        index = 0;
        for (TerminalNode tn : ctx.getTokens(RPCGenParser.ID)) {
            String varName = tn.getText();
            String varType = ctx.var_type(index).getText();

            clientStub.append(varType + " " + varName);

            if (index < ctx.getTokens(RPCGenParser.ID).size() - 1) {
                clientStub.append(", ");
            }
            index++;
        }

        // create method body
        clientStub.append(
                                ") {\n" +
                                "\t\ttry {\n" +
                                    "\t\t\tsocket = new Socket(serverAddress, serverPort);\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not reach server.\");\n" +
                                "\t\t}\n" +
                                "\t\tObjectOutputStream outputStream = null;\n" +
                                "\t\ttry {\n" +
                                    "\t\t\toutputStream = new ObjectOutputStream(socket.getOutputStream());\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not create Object Output Stream.\");\n" +
                                "\t\t}\n" +
                                "\t\t" + className + " copy = new " + className + "();\n"
        );

        // make a copy of the object
        index = 0;
        for (TerminalNode tn : ctx.getTokens(RPCGenParser.ID)) {
            String varName = tn.getText();
            String standardName = capitalizeFirst(varName);
            clientStub.append("\t\tcopy.set" + standardName + "(" + varName + ");\n");
            index++;
        }

        clientStub.append(
                                "\t\ttry {\n" +
                                    "\t\t\toutputStream.writeObject(copy);\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not write object.\");\n" +
                                "\t\t}\n" +
                                "\t\tObjectInputStream inputStream = null;\n" +
                                "\t\ttry {\n" +
                                    "\t\t\tinputStream = new ObjectInputStream(socket.getInputStream());\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not create Object Input Stream\");\n" +
                                "\t\t}\n" +
                                "\t\t" + className + " response = null;\n" +
                                "\t\ttry {\n" +
                                    "\t\t\tresponse = (" + className + ") inputStream.readObject();\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not read object.\");\n" +
                                "\t\t} catch (ClassNotFoundException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not cast to \");\n" +
                                "\t\t}\n" +
                                "\t\ttry {\n" +
                                    "\t\t\tsocket.close();\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"CLIENT: Could not close socket.\");\n" +
                                "\t\t}\n" +
                                "\t\treturn response.getReturnValue();\n" +
                            "\t}\n" +
                        "}\n"
        );
    }

    private void buildServerCode() {

        serverCode = new StringBuilder(
                "import java.io.IOException;\n" +
                "import java.net.ServerSocket;\n" +
                "import java.net.Socket;\n\n" +
                "public class " + this.serviceName + " {\n" +
                "\tpublic static void main(String[] args) {\n" +
                "\t\tint portNumber = 0;\n" +
                "\t\ttry {\n" +
                    "\t\t\tportNumber = Integer.parseInt(args[0]);\n" +
                "\t\t} catch (NumberFormatException ex) {\n" +
                    "\t\t\tSystem.out.println(\"" + serviceName + " usage:\\njava " + serviceName + " [portNumber]\");\n" +
                    "\t\t\tSystem.exit(0);\n" +
                "\t\t}\n" +
                "\t\tServerSocket serverSocket = null;\n" +
                "\t\ttry {\n" +
                "\t\t\tserverSocket = new ServerSocket(portNumber);\n" +
                "\t\t} catch (IOException e) {\n" +
                "\t\t\tSystem.out.println("+"\"[ERROR] Could not create server.\""+");\n" +
                "\t\t}\n" +
                "\t\tfor (;;) {\n" +
                "\t\t\tSocket clientSocket = null;\n" +
                "\t\t\ttry {\n" +
                "\t\t\t\tclientSocket = serverSocket.accept();\n" +
                "\t\t\t\tClientHandler clientHandler = new ClientHandler(clientSocket);\n" +
                "\t\t\t\tclientHandler.start();\n" +
                "\t\t\t} catch (IOException e) {\n" +
                "\t\t\t\tSystem.out.println("+"\"[ERROR] Could not accept resquest from client.\""+");\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\t/* * * * * * * * *\n" +
                "\t\t*   NOT REACHED  *\n" +
                "\t\t* * * * * * * * */\n" +
                "\t}\n" +
                "}\n");
    }

    private void buildServerInfoClass() {
        serverInfoCode = new StringBuilder(
        "import java.io.Serializable;\n" +
        "import java.net.Socket;\n" +
        "public abstract class ServerInfo implements Serializable {\n" +
            "\tprotected Socket socket;\n" +
            "\tprotected String serverAddress;\n" +
            "\tprotected int serverPort;\n" +
            "\tpublic Socket getSocket() {\n" +
                "\t\treturn socket;\n" +
            "\t}\n" +
            "\tpublic void setSocket(Socket socket) {\n" +
                "\t\tthis.socket = socket;\n" +
            "\t}\n" +
            "\tpublic String getServerAddress() {\n" +
                "\t\treturn serverAddress;\n" +
            "\t}\n" +
            "\tpublic void setServerAddress(String serverAddress) {\n" +
                "\t\tthis.serverAddress = serverAddress;\n" +
            "\t}\n" +
            "\tpublic int getServerPort() {\n" +
                "\t\treturn serverPort;\n" +
            "\t}\n" +
            "\tpublic void setServerPort(int serverPort) {\n" +
                "\t\tthis.serverPort = serverPort;\n" +
            "\t}\n" +
        "}\n"
        );
    }

    private void buildServerStubHeader() {
        serverStub = new StringBuilder(
                        "import java.io.IOException;\n" +
                        "import java.io.ObjectInputStream;\n" +
                        "import java.io.ObjectOutputStream;\n" +
                        "import java.net.Socket;\n" +
                        "public class ClientHandler extends Thread {\n" +
                            "\tprivate Socket clientSocket;\n" +
                            "\tpublic ClientHandler(Socket clientSocket) {\n" +
                                "\t\tthis.clientSocket = clientSocket;\n" +
                            "\t}\n" +
                            "\t@Override\n" +
                            "\tpublic void run() {\n" +
                                "\t\tObjectInputStream inputStream = null;\n" +
                                "\t\ttry {\n" +
                                    "\t\t\tinputStream = new ObjectInputStream(clientSocket.getInputStream());\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"SERVER: Client handler Input Stream.\");\n" +
                                "\t\t}\n" +
                                "\t\tObject function = null;\n" +
                                "\t\ttry {\n" +
                                    "\t\t\tfunction = inputStream.readObject();\n" +
                                "\t\t} catch (IOException e) {\n" +
                                    "\t\t\tSystem.out.println(\"SERVER: Client handler IOException.\");\n" +
                                "\t\t} catch (ClassNotFoundException e) {\n" +
                                    "\t\t\tSystem.out.println(\"SERVER: Client handler ClassNotFoundException.\");\n" +
                                "\t\t}\n"
                );
    }

    private void buildServerStubBody(String className, String functionName) {

        if (functionCounter > 0) {
            serverStub.append(
                    "else if (function != null && function instanceof " + className + ") {\n"
            );
        } else {
            serverStub.append(
                    "\t\tif (function != null && function instanceof " + className + ") {\n"
            );
        }
        serverStub.append(
            "\t\t\t" + className + " " + functionName + " = (" + className + ") function;\n" +
            "\t\t\t" + className + "Implementation " + functionName + "Implementation = new " + className + "Implementation(" + functionName + ");\n" +
            "\t\t\t" + functionName + ".setReturnValue(" + functionName + "Implementation." + functionName + "());\n" +
            "\t\t\tObjectOutputStream outputStream = null;\n" +
            "\t\t\ttry {\n" +
                "\t\t\t\toutputStream = new ObjectOutputStream(clientSocket.getOutputStream());\n" +
            "\t\t\t} catch (IOException e) {\n" +
                "\t\t\t\tSystem.out.println(\"SERVER: Could not create Object Output Stream.\");\n" +
            "\t\t\t}\n" +
            "\t\t\ttry {\n" +
                "\t\t\t\toutputStream.writeObject(" + functionName +");\n" +
            "\t\t\t} catch (IOException e) {\n" +
                "\t\t\t\tSystem.out.println(\"SERVER: Could not write object.\");\n" +
            "\t\t\t}\n" +
        "\t\t} "
        );
    }

    private void buildServerStubFooter() {
        serverStub.append(
                "else {\n" +
                    "\t\t\ttry {\n" +
                        "\t\t\t\tthrow new ClassNotFoundException(\"SERVER: Could not find procedure.\");\n" +
                    "\t\t\t} catch (ClassNotFoundException e) {\n" +
                        "\t\t\t\tSystem.out.println(e.getMessage());\n" +
                    "\t\t\t}\n" +
                "\t\t}\n" +
            "\t}\n" +
        "}\n"
        );
    }

    private StringBuilder buildClientStubHeader(String className) {
        return new StringBuilder(
            "import java.io.IOException;\n" +
            "import java.io.ObjectInputStream;\n" +
            "import java.io.ObjectOutputStream;\n" +
            "import java.io.Serializable;\n" +
            "import java.net.Socket;\n" +
            "public class " + className + " extends ServerInfo implements Serializable {\n"
        );
    }

    private StringBuilder buildImplementationStub(String className, String functionName, int returnType) {
        String returnText = (returnType == INT_RET? "int" : "String");

        return new StringBuilder(
        "public class " + className + "Implementation {\n" +
            "\tprivate " + className + " " + functionName + ";\n" +
            "\tpublic " + className + "Implementation(" + className + " " + functionName + ") {\n" +
                "\t\tthis." + functionName + " = " + functionName + ";\n"  +
            "\t}\n" +
            "\tpublic " + returnText +  " " + functionName + "() {\n" +
                "\t\t// ADD YOUR CODE HERE:\n\n" +
            "\t}\n" +
        "}\n"
        );
    }

    public static String capitalizeFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
    }
}
