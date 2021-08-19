
<h1 align="center">
    Diferenças entre variáveis normais, arraylists, temporárias e independentes
</h1>

<p align="center">A API possui atualmente 4 tipos de variáveis, essa documentação explicará detalhadamente o que cada um faz, suas utilidades e aplicações.</p>
<br>
<p align="center">Variáveis dependentes (players) são salvas/descarregadas e carregadas quando o jogador entra ou sai do servidor.</p>

<h2 align="center">
    🔆 Variáveis normais 🔆
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">Ao criar uma variável, ela será armazenada no banco de dados (configurado no config.yml) e será enviada para todos os jogadores. Se o jogador não possuir aquela variável, será atribuída a ele o valor padrão da variável.</p>

<h4 align="center">Lembre-se, uma variável que foi criada com um valor padrão INTEIRO (números) não pode conter valores de texto caso o tipo de banco de dados seja SQLite ou MySQL. Caso você tente mudar, a alteração não será bem sucedida!</h4>

<h3 align="center">
    Como criar
</h3>

<p align="center">
    Adicione esse código ao seu método onEnable() (Não obrigatório, porém o jeito certo).
</p>

```java
new Variable(Plugin plugin, String name, Object default_value, Boolean textvariable);
```

<p align="center">Exemplo de uso</p>

```java
new Variable(this, "coins", 0);
```






<h2 align="center">
    🔆 Variáveis arraylist🔆
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">A variável arraylist tem o funcionamento de uma variável normal (assim como todas as outras), porém ela armazena uma lista de informações em vez de só uma, a sua desvantagem é que ela perde o tipo dos valores, se você armazenar uma lista de jogadores, eles viraram textos (strings) dentro da ArrayVariable.</p>

<h3 align="center">
    Como criar
</h3>

<p align="center">
    Adicione esse código ao seu método onEnable() (Não obrigatório, porém o jeito certo).
</p>

```java
new ArrayVariable(Plugin plugin, String name, Object default_value, Boolean textvariable);
```

<p align="center">Exemplo de uso</p>

```java
new ArrayVariable(this, "coins", new ArrayList<>());
```






<h2 align="center">
    🔆 Variáveis temporárias 🔆
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">É basicamente uma variável normal, porém ela não é armazenada no banco de dados e some em reloads. Porém ela não possui a limitação de armazenar textos em variáveis com o valor padrão inteiro (números), que é uma limitação das variáveis em caso de banco de dados em MySQL ou SQLite.</p>

<h3 align="center">
    Como criar
</h3>

<p align="center">
    Adicione esse código ao seu método onEnable() (Não obrigatório, porém o jeito certo).
</p>

```java
new TempVariable(Plugin plugin, String name, Object default_value, Boolean textvariable);
```

<p align="center">Exemplo de uso</p>

```java
new TempVariable(this, "coins", 0);
```

<h2 align="center">
    🔔 Variáveis independentes 🔔
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">Variáveis independentes podem ser armazenadas sem a dependência de um jogador, podem armazenar variáveis em textos.</p>

<h3 align="center">
    Como criar
</h3>

<p align="center">
    Adicione esses códigos ao seu método onEnable() (Não obrigatório, porém o jeito certo).
</p>

<p align="center">Primeiro, crie o receptor da variável (funcionará como se fosse um "jogador")</p>

```java
new TextVariableReceptor(Plugin plugin, String name);
```

<p align="center">Depois, você precisará criar as variáveis que serão compatíveis com o método de variáveis independentes</p>

```java
// Basicamente, você criará as variáveis normalmente, mas colocará um "true" no final dela.
new Variable(this, "coins", 0, true);
new ArrayVariable(this, "amigos", new ArrayList<>(), true);
new TempVariable(this, "coins", 0, true);
```

<p align="center">Variáveis independentes são salvas/descarregadas e carregadas em um reload.</p>


