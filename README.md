# Projeto de um Ecommerce em uma arquitetura Serverless

> Repositório em construção...

Aplicação construída com a finalidade de estudar os recursos disponíveis na AWS. Estou seguindo o curso disponível no link https://www.udemy.com/course/aws-serverless-nodejs-cdk-pt/, porém não estou utilizando o NodeJS para provisionar os recursos nem construir os códigos das aplicações.  
A arquitetura abaixo ilustra como ficará o projeto ao ser concluído.  
![arquitetura do ecommerce](/images/arquitetura-ecommerce.png)

- A infraestrutura da AWS está sendo criada e automatizada utilizando o [AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/work-with-cdk-java.html). A linguagem utilizada no CDK foi o Java;
- As funções Lambda também estão sendo desenvolvidas na linguagem Java.


### Recursos/tecnologias/ferramentas utilizadas para criar o projeto
- Java 11;
- Maven 3.8.6;
- Docker 20.10.17;
- aws cdk 2.40.0.

### Comandos interessantes do AWS CDK
Os comandos devem ser executados dentro do diretório `cdk-infra`, pois é onde estão configuradas todas as stacks do projeto:
- `cdk ls`: lista todas as stacks presentes no projeto;
- `cdk deploy --all`: faz o deploy na sua conta AWS de todas as stacks presentes no projeto;
- `cdk destroy --all`: remove todos (todas as stacks) os recursos criados na AWS.

### Operações expostas pelo API Gateway ECommerceAPI

A última coluna destaca a função lambda responsável pela operação

| Operação | Endpoint | Verbo HTTP | Lambda |
| -------- | -------- | ---------- | ------ |
| Criar um produto | `/products` | POST | products-admin-lambda |
| Buscar um produto pelo id | `/products/{id_produto}` | GET | products-lambda |
| Alterar um produto pelo id | `/products/{id_produto}` | PUT | products-admin-lambda |
| Apagar um produto pelo id | `/products/{id_produto}` | DELETE | products-admin-lambda |

### Modelagem das tabelas no DynamoDB

#### Tabela de produtos
No DynamoDB precisamos apenas de definir a chave primária da tabela, no entanto para facilitar a codificação da aplicação, vamos modelar uma tabela de produtos (`ProductsTable`) com os seguintes atributos:

| Atributo | Tipo no DynamoDB |
| -------- | ---------------- |
| `Id` (chave primária e hash key) | String |
| `Model` | String |
| `URLImage` | String |
| `Code` | String |
| `Price` | Number |
| `ProductName` | String |


#### Tabela de eventos
O objetivo desta tabela é manter um registro de alterações realizadas na `ProductsTable`. Tal tabela é chamada `EventsTable` e possui os seguintes atributos:

| Atributo                            | Tipo no DynamoDB |
| ----------------------------------- | ---------------- |
| `Code` (hash key)                   | String           |
| `EventTypeAndTimestamp` (range key) | String           |
| `Email` (de quem fez a alteração)   | String           |
| `CreatedAt`                         | Number           |
| `RequestId`                         | String           |
| `EventType`                         | String           |
| `Ttl`                               | Number           |
| `Info`: {`ProductId`, `Price`}      | Map              |

em especial, `ProductId` é do tipo String e `Price` do tipo Number.

### Como as lambdas estão sendo empacotadas para o deploy

Dentro do projeto `cdk-infra` deve existir um diretório `lambdas` que é para onde os arquivos `.jar` das lambdas, gerados pelo comando `mvn clean package`, devem ser copiados. Exemplificando com a classe `ProductsAppStack`:
```java
public class ProductsAppStack extends Stack {
    /* ... */
    .code(Code.fromAsset("lambdas/products/products-lambda-1.0-SNAPSHOT.jar"))
    /* ... */
}
```
como podemos ver do trecho de código acima, é esperado que dentro de `cdk-infra` exista um diretório `lambdas/products` com o arquivo `products-lambda-1.0-SNAPSHOT.jar`.  
:warning: Essa abordagem só é possível enquanto os arquivos forem menor que 50 MB, como descrito em [Lambda deployment packages](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-package.html).


### Observações
- Para habilitar o X-Ray bastou apenas eu adicionar as dependências no `pom.xml`, não precisei adicionar nenhum novo código nas aplicações das Lambdas. Já no cdk eu precisei habilitar o tracing.
- Para habilitar o LambdaInsights é preciso ficar atento a qual versão será utilizar no projeto do cdk, pois não há disponibilidade em todas as regiões, consultem em [lambda insights versions for x86-64 plataforms](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/Lambda-Insights-extension-versionsx86-64.html). Será adicionado de forma transparente uma layer a função lambda.


### Referências
- [Creating a serverless application using the AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/serverless_example.html)
- [AWS Lambda Developer Guide](https://github.com/awsdocs/aws-lambda-developer-guide)
- [Java sample applications for AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/java-samples.html)
- [Java examples](https://github.com/aws-samples/aws-cdk-examples/tree/master/java)
- [Using AWS Lambda with Amazon API Gateway](https://docs.aws.amazon.com/lambda/latest/dg/services-apigateway.html)
- [AWS Lambda function handler in Java](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
- [Deploy Java Lambda functions with .zip or JAR file archives](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)
- [Instrumenting Java code in AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/java-tracing.html)
- [AWS X-Ray SDK for Java](https://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java.html)