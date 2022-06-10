
<h1 align="center">
    Como criar variáveis
</h1>

<h2 align="center">
    ⚠ Atenção ⚠
</h2>

<p align="center">
 Antes de prosseguir, você precisa aprender a criar as tabelas ([clique](https://github.com/ItsLaivy/DataAPI/edit/updates/docs/Tabela%20de%20variáves.md)) e receptores ([clique](https://github.com/ItsLaivy/DataAPI/edit/updates/docs/Receptores%20de%20variáveis.md)).
</p>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">Ao criar uma variável, ela será armazenada no banco de dados e será enviada para todos os receptores. Se o receptor não possuir aquela variável, será atribuída a ele o valor padrão da variável.</p>

<h3 align="center">
    Como criar
</h3>

<p align="center">
    Adicione esse código ao seu método onEnable() (Não é obrigatório ser no onEnable(), porém é o melhor jeito).
</p>

```java
new Variable(String name, Table table, Serializable defaultValue);
new Variable(String name, Table table, Serializable defaultValue, Boolean saveToDatabase);
new Variable(Plugin plugin, String name, Table table, Serializable defaultValue, Boolean saveToDatabase);
new Variable(Plugin plugin, String name, Table table, Serializable defaultValue, Boolean saveToDatabase, Boolean messages);
```

<p align="center">Exemplo de uso</p>

```java
new Variable("coins", table, 0L);
new Variable("level", table, null);

// A variável a seguir não será salva no banco de dados, ficará somente na memória. São chamadas variáveis temporárias e são resetadas para o valor padrão definido sempre que o receptor é carregado
new Variable(this, "level", table, null, false);
// Basta incrementar um "false" no fim, e assim não salvará mais no banco de dados.
```
<p align="center">Todas as variáveis, assim que criadas, são enviadas a todos os receptores ativos.</p>