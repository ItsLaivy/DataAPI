<h1 align="center">
    Como obter os valores de uma variável
</h1>

<p align="center">Pode parecer um pouco complicado de obter os valores de uma variável, porém é mais fácil do que parece!</p>

<h2 align="center">
    🌀 Variáveis dependentes (de jogadores) 🌀
</h2>

<h3 align="center">
    🔆 Variáveis normais/temporárias 🔆
</h3>
<p align="center">Para obter dados de uma variável normal ou temporária use o método</p>

```java
getPlayerTypeVariableValue(Plugin plugin, String name, Player player);
```

<p align="center">Exemplo</p>

```java
Bukkit.broadcastMessage(getPlayerTypeVariableValue(plugin, "coins", player).toString());
```
<p align="center">Lembrando que o valor retornado é OBJECT, porém sempre utilize o toString().</p>
<h3 align="center">
    🔆 Variáveis arraylist 🔆
</h3>
<p align="center">Para poder executar um foreach() em uma variável arraylist, use o método</p>

```java
getPlayerTypeArrayVariable(Plugin plugin, String name, Player player);
```

<p align="center">Exemplo</p>

```java
int row = 0;
for (String value : getPlayerTypeArrayVariable(plugin, "amigos", player)) {
    row++;
    Bukkit.broadcastMessage("Amigo número " + row + ": " + value);
}
```
<p align="center">Lembrando que variáveis arraylist também podem ser resgatadas com o método getPlayerTypeVariableValue(), porém não podem ser iteradas.</p>


<h2 align="center">
    🔔 Variáveis independentes 🔔
</h2>

<h3 align="center">
    🔆 Variáveis normais/temporárias 🔆
</h3>
<p align="center">Para obter dados de uma variável normal ou temporária use o método</p>

```java
getTextTypeVariableValue(Plugin plugin, String name, String textVariableName);
```

<p align="center">Exemplo</p>

```java
Bukkit.broadcastMessage(getTextTypeVariableValue(plugin, "coins", "nome_da_variável_independente").toString());
```
<p align="center">Lembrando que o valor retornado é OBJECT, porém sempre utilize o toString().</p>
<h3 align="center">
    🔆 Variáveis arraylist 🔆
</h3>
<p align="center">Para poder executar um foreach() em uma variável arraylist, use o método</p>

```java
getTextTypeArrayVariable(Plugin plugin, String name, Player player);
```

<p align="center">Exemplo</p>

```java
int row = 0;
for (String value : getTextTypeArrayVariable(plugin, "amigos", player)) {
    row++;
    Bukkit.broadcastMessage("Amigo número " + row + ": " + value);
}
```
<p align="center">Lembrando que variáveis arraylist também podem ser resgatadas com o método getPlayerTypeVariableValue(), porém não podem ser iteradas.</p>

