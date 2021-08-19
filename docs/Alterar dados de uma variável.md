<h1 align="center">
    Como alterar os dados de uma variável
</h1>

<p align="center">Esse já é um pouco mais complicado, mas com um pouco de prática você aprende :)</p>

<h2 align="center">
    ⚠️ Atenção ⚠️
</h2>

<p align="center">Se o seu banco de dados (definido na config.yml) for MySQL ou SQLite: Se uma variável (não incluindo as temporárias nem arraylists) possuirem um defaultvalue (valor padrão) inteiro (números) ela não pode ser um texto (pois buga no banco de dados), se você tentar alterar usando o setVariable() irá falhar e retornar false.</p>

<h2 align="center">
    🌀 Variáveis dependentes (de jogadores) 🌀
</h2>

```java
setPlayerTypeVariableValue(Plugin plugin, String name, Player player, Object value);
```

<p align="center">Exemplo</p>

```java
setPlayerTypeVariableValue(plugin, "nome_da_variável", player, 50000);
```

<p align="center">Lembrando que o método de definir variável retorna um boolean (true ou false) indicando se o procedimento foi bem ou mal sucedido.</p>

```java
if (setPlayerTypeVariableValue(plugin, "nome_da_variável", player, 50000)) {
	Bukkit.broadcastMessage("Variável alterada com sucesso.");
} else {
	Bukkit.broadcastMessage("Ocorreu um erro na alteração da variável.");
}
```

<h2 align="center">
    🔔 Variáveis independentes 🔔
</h2>

```java
setTextTypeVariableValue(Plugin plugin, String name, String textVariableName, Object value);
```

<p align="center">Exemplo</p>

```java
setTextTypeVariableValue(plugin, "nome_da_variável", "nome_da_variável_independente", 50000);
```

<p align="center">Lembrando que o método de definir variável retorna um boolean (true ou false) indicando se o procedimento foi bem ou mal sucedido.</p>

```java
if (setTextTypeVariableValue(plugin, "nome_da_variável", "nome_da_variável_independente", 50000)) {
	Bukkit.broadcastMessage("Variável alterada com sucesso.");
} else {
	Bukkit.broadcastMessage("Ocorreu um erro na alteração da variável.");
}
```