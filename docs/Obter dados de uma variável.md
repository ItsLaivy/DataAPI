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
    🔆 Variáveis normais 🔆
</h3>
<p align="center">Para obter dados de uma variável normal use o método</p>

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
    🔆 Parseando os valores de uma variável 🔆
</h3>

<p align="center">Ao coletar o valor de uma variável, o retorno será VariableReturnModule.class, precisamos transformar isso em um valor legível...</p>


```java
// Retorna String
value.asString();
// Retorna Integer
value.asInt();
// Retorna Double
value.asDouble();
// Retorna Long
value.asLong();
// Retorna List<Object>
value.asList();
// Retorna Byte
value.asByte();
// Retorna Boolean
value.asBoolean();

// Exemplos
if (getVariableValue("logado", receptor).asBoolean) {
    System.out.println("§aVocê está logado!");
}
// ou
System.out.println("§aVocê possui R$" + getVariableValue("money", receptor).asDouble);
```
