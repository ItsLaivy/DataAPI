<h1 align="center">
    Como obter os valores de uma variável
</h1>

<p align="center">Pode parecer um pouco complicado de obter os valores de uma variável, porém é mais fácil do que parece.</p>

<h2 align="center">
    ⚠ Atenção ⚠
</h2>

<p align="center">
 Antes de prosseguir, você precisa aprender a criar as tabelas ([clique](https://github.com/ItsLaivy/DataAPI/edit/updates/docs/Tabela%20de%20variáves.md)) e receptores ([clique](https://github.com/ItsLaivy/DataAPI/edit/updates/docs/Receptores%20de%20variáveis.md)).
</p><br>

<h2 align="center">
    🔆 Obter valores 🔆
</h3>
<p align="center">Para obter dados de uma variável use o método</p>

```java
new VariableValue<T extends Serializable>(Receptor receptor, Plugin plugin, String name).getValue();
new VariableValue<T extends Serializable>(Receptor receptor, ActiveVariable activeVariable).getValue();
new VariableValue<T extends Serializable>(Receptor receptor, String name).getValue();

// Lembrando que T precisa implementar a interface Serializable
// O valor T de uma variável precisa ser o mesmo definido anteriormente
```

<p align="center">Exemplo</p>

```java
Bukkit.broadcastMessage("Você possui: R$" + (new VariableValue<Long>(receptor, "money").getValue()));
```