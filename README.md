<p align="center">
 <a href="https://github.com/LaivyTLife/DataAPI/issues"><img src="https://img.shields.io/github/issues/LaivyTLife/DataAPI?style=for-the-badge"></img></a>
 <a href="https://github.com/LaivyTLife/DataAPI/graphs/contributors"><img src="https://img.shields.io/github/contributors/LaivyTLife/DataAPI?style=for-the-badge"></img></a>
 <a href="https://github.com/LaivyTLife/DataAPI/blob/master/LICENSE"><img src="https://img.shields.io/github/license/LaivyTLife/DataAPI?style=for-the-badge"></img></a>
 <a><img src="https://img.shields.io/github/downloads/LaivyTLife/DataAPI/total?style=for-the-badge"></img></a>
 <a href="https://github.com/LaivyTLife/DataAPI/releases"><img src="https://img.shields.io/github/v/release/LaivyTLife/DataAPI?include_prereleases&style=for-the-badge"></img></a>
</p>

<h1 align="center">
    Sobre
</h1>

<p align="center">É uma API para armazenar jogadores. Bem simples, porém útil para servidores com plugins próprios e fixos, será mantido sempre bem atualizada.</p>

<h2 align="center">
    Documentação
</h2>

<h3 align="center">
    Dependência
</h3>

<p align="center">
    Adicione a dependência na <b>plugin.yml</b> de seu plugin
</p>

```yaml
depend: [LvDataAPI]
```

<h3 align="center">
    Como criar uma variável
</h3>

<p align="center">
    Adicione esse método em seu <b>onEnable()</b>:
</p>

```java
new Variable(Plugin plugin, String name, Object value);
new Variable(this, "NOME", "VALOR");
```

</br>
<p align="center">
    Como <b>pegar o valor</b> de uma variável de um jogador
</p>

```java
static Object getVariable(Plugin plugin, Player player, String name)
Object var = getVariable(this, player, "nome-da-variável");
```
</br>
<p align="center">
    Como <b>definir o valor</b> de uma variável de um jogador
</p>

```java
static Boolean setVariable(Plugin plugin, Player player, String name, Object value)
setVariable(this, player, "nome-da-variável", "novo-valor")
```

</br>
<p align="center">
    Como ver se um jogador está carregado ou não
</p>

```java
static Boolean isLoaded(Player player)
if (isLoaded(player)) {
  player.sendMessage("Você está carregado!");
}
```

</br>
<p align="center">
    Como descarregar/carregar um jogador
    </br>Lembrando que o jogador já é automaticamente carregado na entrada e automaticamente descarregado na saída.
</p>

```java
static void loadPlayer(Player player)
loadPlayer(player);

static void unloadPlayer(Player player)
unloadPlayer(player);
```

</br>
<p align="center">
    Como salvar os dados de um jogador
    </br>Lembrando que o jogador já é salvo automaticamente em um intervalo de segundos configurável na <b>config.yml</b> do plugin.
</p>

```java
static void savePlayer(Player player)
savePlayer(player);
```

</br>
</br>

<h2 align="center">
    Contato/Reportar bugs
</h2>

<p align="center">
    Para reportar um bug ou enviar uma sugestão, acesse a <a href="https://github.com/LaivyTLife/DataAPI/issues">área de issues</a> e crie um novo.
</p>
</br>
<p align="center">
 Discord: DanielZinh#7616
</p>