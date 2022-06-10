<h1 align="center">
    Como criar uma tabela de variáveis
</h1>

<p align="center">Uma tabela de variáveis é essencial ser criada para armazenar as variáveis e seus receptores.</p>

<h2 align="center">
    ⚠ Atenção ⚠
</h2>

<p align="center">
 Após esse, é necessários aprender sobre receptores ([clique](https://github.com/ItsLaivy/DataAPI/edit/updates/docs/Receptores%20de%20variáveis.md)).
</p><br>

<h2 align="center">
    🔆 Table.class 🔆
</h3>
<p align="center">Para criar uma tabela de variáveis, utilize o método</p>

```java
new Table(Database database);
new Table(String name, Database database);
new Table(Plugin plugin, String name, Database database);
```

<p align="center">Exemplo</p>

```java
Table tablePlayers = new Table(database, "players_data");
new Variable(this, "coins", tablePlayers, 0);

// Todos os receptores dessa tabela, terão a variável "coins".
```
