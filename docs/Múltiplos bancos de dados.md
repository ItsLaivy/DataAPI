
<h1 align="center">
    Criar um banco de dados para utilizar as tabelas
</h1>

<h2 align="center">
    🔆 Banco de dados SQLite 🔆
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">Banco de dados SQLite são armazenados em uma pasta dentro do próprio plugin, não necessitando de um programa externo como XAMPP (no caso do MySQL), é mais rápido em alguns fatores dependendo da ocasião e de seu uso</p>

<h3 align="center">
    Como criar
</h3>

```java
new SQLiteDatabase();
new SQLiteDatabase(String name);
new SQLiteDatabase(String name, String path);
new SQLiteDatabase(Plugin plugin, String name);
new SQLiteDatabase(Plugin plugin, String name, String path);
```

<p align="center">Exemplo de uso</p>

```java
SQLiteDatabase database = new SQLiteDatabase("Nome");
Table tabela = new Table(database);
```

<h2 align="center">
    🔆 Banco de dados MySQL 🔆
</h2>

<h3 align="center">
    Como funcionam
</h3>

<p align="center">Bancos de dados MySQL necessitam de um servidor DB externo (como XAMPP) porém podem se comunicar com múltiplos servidores ao mesmo tempo, você pode por exemplo criar uma database em um servidor X, e se conectar nessa database e gerencia-la por um servidor Z (impossível no SQLite sem programas alternativos)</p>


<h3 align="center">
    Como criar
</h3>

```java
new MySQLDatabase(String address, Integer port, String user, String password);
new MySQLDatabase(String name, String address, Integer port, String user, String password);
new MySQLDatabase(Plugin plugin, String name, String address, Integer port, String user, String password);
```


<p align="center">Exemplo de uso</p>

```java
// Observação: Porta null = 3306
MySQLDatabase database = new MySQLDatabase("localhost", null, "root", "senha");
Table tabela = new Table(database);
```