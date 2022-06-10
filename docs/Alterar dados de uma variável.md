<h1 align="center">
    Como alterar os dados de uma variável
</h1>

<p align="center">Esse já é um pouco mais complicado, mas com um pouco de prática você aprende :)</p>

<h2 align="center">
    🛠 Alterar dados de uma variável 🛠
</h2>

```java
new VariableValue<T extends Serializable>(Receptor receptor, Plugin plugin, String name).setValue(T value);
new VariableValue<T extends Serializable>(Receptor receptor, ActiveVariable activeVariable).setValue(T value);
new VariableValue<T extends Serializable>(Receptor receptor, String name).setValue(T value);

// Lembrando que T precisa implementar a interface Serializable

```

<p align="center">Exemplo</p>

```java
new VariableValue<String>(receptor, "variavel_de_texto").setValue("valor");
new VariableValue<ArrayList<UUID>()>(receptor, "amigos").setValue(listaDeAmigos);
```
