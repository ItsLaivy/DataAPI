
<h1 align="center">
    Diferenças entre variáveis normais, arraylists e temporárias
</h1>

<p align="center">A API possui atualmente 3 tipos de variáveis, essa documentação explicará detalhadamente o que cada um faz, suas utilidades e aplicações.</p>
<br>

<h2 align="center">
    ⚠ Atenção ⚠
</h2>

<p align="center">
 Antes de prosseguir, você precisa aprender a criar as tabelas ([clique](https://github.com/LaivyTLife/DataAPI/edit/updates/docs/Tabela%20de%20variáves.md)) e receptores ([clique](https://github.com/LaivyTLife/DataAPI/edit/updates/docs/Receptores%20de%20variáveis.md)).
</p><br>

<h2 align="center">
    🔆 Variáveis normais 🔆
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">Ao criar uma variável, ela será armazenada no banco de dados (configurado no config.yml) e será enviada para todos os receptores. Se o receptor não possuir aquela variável, será atribuída a ele o valor padrão da variável.</p>

<h3 align="center">
    Como criar
</h3>

<p align="center">
    Adicione esse código ao seu método onEnable() (Não obrigatório, porém o jeito certo).
</p>

```java
new Variable(String name, TableCreationModule table, Object defaultValue);
new Variable(Plugin plugin, String name, TableCreationModule table, Object defaultValue);
new Variable(String name, TableCreationModule table, Object defaultValue, Boolean saveToDatabase);
new Variable(Plugin plugin, String name, TableCreationModule table, Object defaultValue, Boolean saveToDatabase);
```

<p align="center">Exemplo de uso</p>

```java
new Variable(this, "coins", table, 0);
new Variable(this, "level", table, null);

// A variável a seguir não será salva no banco de dados, ficará somente na memória. São chamadas variáveis temporárias e são resetadas para o valor padrão definido sempre que o receptor é carregado
new Variable(this, "level", table, null, false);
// Basta incrementar um "false" no fim, e assim não salvará mais no banco de dados.
```
<p align="center">Todas as variáveis, assim que criadas, são enviadas a todos os receptores ativos.</p>





<h2 align="center">
    📄 Variáveis ArrayList 📄
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">A variável arraylist tem o funcionamento de uma variável normal (assim como todas as outras), porém ela armazena uma lista de informações em vez de só uma, a sua desvantagem é que ela perde o tipo dos valores, se você armazenar uma lista de jogadores, eles virarão textos (strings) dentro da ArrayVariable, e terá que transforma-las em players novamente.</p>

<h3 align="center">
    Como criar
</h3>
<p align="center">
    Adicione esse código ao seu método onEnable() (Não obrigatório, porém o jeito certo).
</p>

```java
new ArrayVariable(String name, TableCreationModule table, Object defaultValue);
new ArrayVariable(Plugin plugin, String name, TableCreationModule table, Object defaultValue);
new ArrayVariable(String name, TableCreationModule table, Object defaultValue, Boolean saveToDatabase);
new ArrayVariable(Plugin plugin, String name, TableCreationModule table, Object defaultValue, Boolean saveToDatabase);
```

<p align="center">Exemplo de uso</p>

```java
new ArrayVariable(this, "amigos_lista", null); // Uma lista vazia de amigos


// A variável a seguir não será salva no banco de dados, ficará somente na memória. São chamadas variáveis temporárias e são resetadas para o valor padrão definido sempre que o receptor é carregado
new ArrayVariable(this, "amigos_lista", table, null, false);
// Basta incrementar um "false" no fim, e assim não salvará mais no banco de dados.
```
