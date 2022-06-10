<h1 align="center">
    Receptores de variáveis
</h1>

<p align="center">Um receptor de variáveis, como o próprio nome já diz, recebe varíaveis e armazena elas, ela pode ser um player, ou até um texto.</p>

<h2 align="center">
    🔆 Receptores de variáveis 🔆
</h3>
<p align="center">Para criar um receptor de variáveis, utilize o método</p>

```java
new Receptor(OfflinePlayer player, Table table);
new Receptor(String name, String bruteId, Table table);
new Receptor(Plugin plugin, String name, String bruteId, Table table);
```

<p align="center">Exemplo</p>

```java
new Receptor(player, tablePlayers);
new Receptor(this, "textName", "textBruteId", tablePlayers);
```

<h2 align="center">
   Observe
</h3>
<p align="center">O parâmetro "bruteId" pode ser o UUID de um jogador, ou identificador de um receptor não-jogador, você usará o bruteId para pegar aquele receptor no banco de dados.</p>