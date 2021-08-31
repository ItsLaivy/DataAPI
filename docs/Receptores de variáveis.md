<h1 align="center">
    Receptores de variáveis
</h1>

<p align="center">Um receptor de variáveis, como o próprio nome já diz, recebe varíaveis e armazena elas, ela pode ser um player, ou até um texto.</p>

<h2 align="center">
    🔆 Receptores de variáveis 🔆
</h3>
<p align="center">Para criar um receptor de variáveis, utilize o método</p>

```java
new VariableReceptor(Plugin plugin, String name, String bruteId, VariablesTable table);
// ou
new VariableReceptor(Plugin plugin, Player player, VariablesTable table);
```

<p align="center">Exemplo</p>

```java
VariablesTable tablePlayers = new VariablesTable(this, "players_data");

new VariableReceptor(this, player, tablePlayers);
new VariableReceptor(this, "textName", "textBruteId", tablePlayers);

new Variable(this, "coins", tablePlayers, 0);
```

<h2 align="center">
   Observe
</h3>
<p align="center">O parâmetro "bruteId" pode ser o UUID de um jogador, ou identificador de um receptor não-jogador, você usará o bruteId para pegar aquele receptor no banco de dados. </p>

```java
getVariableReceptor(Plugin plugin, String bruteid, VariablesTable table);
// ou
getVariableReceptor(Plugin plugin, Player player, VariablesTable table);
```
<p align="center">Exemplo</p>

```java
Bukkit.broadcastMessage(getVariableValue("coins", getVariableReceptor(plugin, player, tablePlayers)).toString());
// Também funciona:
Bukkit.broadcastMessage(getVariableValue("coins", getVariableReceptor(plugin, player.getUniqueId.toString(), tablePlayers)).toString());
```