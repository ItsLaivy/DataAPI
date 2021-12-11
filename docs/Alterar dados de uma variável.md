<h1 align="center">
    Como alterar os dados de uma variável
</h1>

<p align="center">Esse já é um pouco mais complicado, mas com um pouco de prática você aprende :)</p>

<h2 align="center">
    🛠 Redefinir dados de uma variável de um receptor 🛠
</h2>

```java
setVariableValue(String name, VariableReceptor receptor, Object value);
// ou
setVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table, Object value)
// ou
setVariableValue(Plugin plugin, String name, Player player, VariablesTable table, Object value)
```

<p align="center">Exemplo</p>

```java
// Respectivamente com a documentação acima

setVariableValue("amigos", getVariableReceptor(plugin, player.getUniqueId().toString(), tablePlayers), new ArrayList<>());
// ou
setVariableValue(plugin, "amigos", player.getUniqueId().toString(), tablePlayers, new ArrayList<>())
// ou
setVariableValue(plugin, "amigos", player, tablePlayers, new ArrayList<>())
```

<p align="center">Lembrando que o método de definir variável retorna um boolean (true ou false) indicando se o procedimento foi bem ou mal sucedido.</p>

```java
if (setVariableValue(plugin, "amigos", player, tablePlayers, new ArrayList<>())) {
	Bukkit.broadcastMessage("Variável alterada com sucesso.");
} else {
	Bukkit.broadcastMessage("Ocorreu um erro na alteração da variável.");
}
```

<h2 align="center">
    🛠 Implemetar e decrementar dados de uma variável de um receptor 🛠
</h2>

```java
addVariableValue(String name, VariableReceptor receptor, Object value);
// ou
addVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table, Object value)
// ou
addVariableValue(Plugin plugin, String name, Player player, VariablesTable table, Object value)
```


```java
removeVariableValue(String name, VariableReceptor receptor, Object value);
// ou
removeVariableValue(Plugin plugin, String name, String bruteid, VariablesTable table, Object value)
// ou
removeVariableValue(Plugin plugin, String name, Player player, VariablesTable table, Object value)
```

**Observação:** ao adicionar o valor a uma variável usando o método **addVariableValue()**, o valor será adicionado mesmo que já contenha eles, e possuirá dois valores iguais. Ao remover o valor de uma variável usando o método **removeVariableValue()**, mesmo que possua vários iguais, só removerá UM por vez.
