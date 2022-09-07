# Projeto de um Ecommerce em uma arquitetura Serverless

> Repositório em construção...

A arquitetura abaixo ilustra como ficará o projeto ao ser concluído.  
![arquitetura do ecommerce](/images/arquitetura-ecommerce.png)

- A infraestrutura da AWS está sendo criada e automatizada utilizando o [AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/work-with-cdk-java.html). A linguagem utilizada no CDK foi o Java;
- As funções Lambda também estão sendo desenvolvidas na linguagem Java.


### Recursos/tecnologias/ferramentas utilizadas para criar o projeto
- Java 11;
- Maven 3.8.6;
- Docker 20.10.17;
- aws cdk 2.38.1.

### Comandos interessantes do AWS CDK
Os comandos devem ser executados dentro do diretório cdk-infra.
- `cdk ls`: lista todas as stacks presentes no projeto;
- `cdk deploy --all`: faz o deploy na sua conta AWS de todas as stacks presentes no projeto;
- `cdk destroy --all`: remove todos (todas as stacks) os recursos criados na AWS.