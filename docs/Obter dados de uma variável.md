<h1 align="center">
    Como obter os valores de uma variável
</h1>

<p align="center">Pode parecer um pouco complicado de obter os valores de uma variável, porém é mais fácil do que parece.</p>

<h2 align="center">
    ⚠ Atenção ⚠
</h2>

<p align="center">
 Antes de prosseguir, você precisa aprender a criar as tabelas ([clique](https://github.com/LaivyTLife/DataAPI/edit/updates/docs/Tabela%20de%20variáves.md)) e receptores ([clique](https://github.com/LaivyTLife/DataAPI/edit/updates/docs/Receptores%20de%20variáveis.md)).
</p><br>

<h2 align="center">
    🔆 Variáveis normais/temporárias 🔆
</h3>
<p align="center">Para obter dados de uma variável normal ou temporária use o método</p>

```java
getVariableValue(String name, VariableReceptor receptor);
// ou
getVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table);
// ou
getVariableValue(Plugin plugin, String name, Player player, VariablesTable table);
```

<p align="center">Exemplo</p>

```java
// Respectivamente com a documentação acima

Bukkit.broadcastMessage(getVariableValue("coins", getVariableReceptor(plugin, player.getUniqueId().toString(), tablePlayers)).toString());
// ou
Bukkit.broadcastMessage(getVariableValue(plugin, "coins", player.getUniqueId().toString(), tablePlayers).toString());
// ou
Bukkit.broadcastMessage(getVariableValue(plugin, "coins", player, tablePlayers).toString());
```
<p align="center">Lembrando que o valor retornado é OBJECT, porém sempre utilize o toString().</p>
<h2 align="center">
    🔆 Variáveis arraylist 🔆
</h2>

<h3 align="center">
    ⚠ Atenção ⚠
</h3>
<p align="center">Uma variável ArrayList também pode ser pega utilizando o getVariableValue(), porém só com esse método ele pode ser iterado.</p>

<br>
<p align="center">Para poder executar um foreach() em uma variável arraylist, use o método</p>

```java
getVariableArray(String name, VariableReceptor receptor);
// ou
getVariableArray(Plugin plugin, String name, Player player, VariablesTable table);
// ou
getVariableArray(Plugin plugin, String name, String bruteId, VariablesTable table);
```

<p align="center">Exemplo</p>

```java
// Respectivamente com a documentação acima

int row = 0;
for (String value : getVariableArray("amigos", getVariableReceptor(plugin, player.getUniqueId().toString(), tablePlayers))) {
    row++;
    Bukkit.broadcastMessage("Amigo número " + row + ": " + value);
}
// ou
int row = 0;
for (String value : getVariableArray(plugin, "amigos", player, tablePlayers)) {
    row++;
    Bukkit.broadcastMessage("Amigo número " + row + ": " + value);
}
// ou
int row = 0;
for (String value : getVariableArray(plugin, "amigos", player.getUniqueId().toString(), tablePlayers)) {
    row++;
    Bukkit.broadcastMessage("Amigo número " + row + ": " + value);
}
```
<p align="center">Relembrando que variáveis arraylist também podem ser resgatadas com o método getVariableValue(), porém não podem ser iteradas.</p>

<h2 align="center">
    Como definir os valores de uma variável
</h2>

<p align="center">
 Clique aqui para ver a documentação sobre como definir o valor de uma variável de um receptor.
</p><br>

<h2 align="center">
    🔆 Verificar se uma variável está em branco 🔆
</h2>

<p align="center">
 Retorna uma resposta (boolean) se a variável do receptor está em branco (null) ou não.
</p>

<h2 align="center">
    ⚠ Atenção ⚠
</h2>

<p align="center">
Se você quer verificar se uma variável está definida no receptor ou não, esse método não funciona!
</p><br>


```java
isVariableValueNull(ActiveVariableLoader variable);
// ou
isVariableValueNull(Plugin plugin, String name, String bruteId, VariablesTable table);
// ou
isVariableValueNull(Plugin plugin, String name, Player player, VariablesTable table);
```

<p align="center">Exemplo</p>

```java
// Respectivamente com a documentação acima

Bukkit.broadcastMessage(isVariableValueNull(getVariable(plugin, "amigos", player.getUniqueId().toString(), tablePlayers)).toString());
// ou
Bukkit.broadcastMessage(isVariableValueNull(plugin, "amigos", player.getUniqueId().toString(), tablePlayers).toString());
// ou
Bukkit.broadcastMessage(isVariableValueNull(plugin, "amigos", player, tablePlayers).toString());
```