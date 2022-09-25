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

| Operação | Endpoint | Verbo HTTP |
| -------- | -------- | ---------- |
| Criar um produto | `/products` | POST |
| Listar todos os produtos | `/products` | GET |
| Buscar um produto pelo id | `/products/{id_produto}` | GET |
| Alterar um produto pelo id | `/products/{id_produto}` | PUT |
| Apagar um produto pelo id | `/products/{id_produto}` | DELETE |

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