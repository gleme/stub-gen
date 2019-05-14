Dependencias: 
	- Java 8 instalado
	- JDK 1.8 instalada e configurada
	- javac e jar at variável $PATH
	- $JAVA_HOME configurado

Passos para compilar e executar o gerador:

1. Compile o gerador de stubs com make 
	
2. Execute o gerador com ./RPCGen [Arquivo de Entrada] <Nome da classe Servidor>

3. O diretório gen conterá todos os stubs gerados a partir do arquivo de entrada
especificado
	
Exemplo: 
	$ ./RPCGen sample/input.remote CryptoService

Passos para compilar e executar a aplicação desenvolvida:

1. Compile a aplicação com make app

2. Crie o jar da aplicação e do servidor com make jarapp

3. Execute o servidor de serviços com java -jar Server.jar [Numero da Porta]

4. Execute a aplicação com java -jar App.jar





