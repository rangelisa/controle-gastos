# 💸 Controle de Gastos

Sistema completo de controle financeiro pessoal com backend em **Spring Boot** e frontend em **React + Vite**.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-green?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-19-blue?style=flat-square&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow?style=flat-square)

---

## 📋 Funcionalidades

- 🔐 Autenticação com JWT (login e cadastro)
- 💰 Cadastro, edição e exclusão de gastos
- 📊 Dashboard com gráficos por categoria e por mês
- 📅 Filtros por mês, período e categoria
- 📈 Estatísticas: média, maior e menor gasto
- ⚠️ Alerta de gastos com limite configurável
- 📥 Exportação de CSV compatível com Excel e Power BI
- 🔒 Cada usuário vê apenas seus próprios gastos

---

## 🛠️ Tecnologias

### Backend
| Tecnologia | Versão |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.3 |
| Spring Security | 7.0.3 |
| Spring Data JPA | 4.0.3 |
| Hibernate | 7.2.4 |
| MySQL Connector | 9.6.0 |
| jjwt (JWT) | 0.12.5 |
| Lombok | 1.18.42 |

### Frontend
| Tecnologia | Versão |
|---|---|
| React | 19 |
| Vite | 8 |
| React Router DOM | 7 |
| Recharts | 2 |

---

## 📁 Estrutura do Projeto

```
controle-gastos/
│
├── backend/
│   └── src/main/java/com/isabella/controle_gastos/
│       ├── ControleGastosApplication.java
│       ├── Gasto.java
│       ├── GastoController.java
│       ├── GastoRepository.java
│       └── login/
│           ├── AuthController.java
│           ├── CorsConfig.java
│           ├── JwtFilter.java
│           ├── JwtUtil.java
│           ├── SecurityConfig.java
│           ├── SpringSecurityConfig.java
│           ├── Usuario.java
│           └── UsuarioRepository.java
│
└── frontend/
    └── src/
        ├── App.jsx
        ├── api.js
        ├── main.jsx
        ├── index.css
        ├── components/
        │   ├── Layout.jsx
        │   └── Layout.css
        └── pages/
            ├── Login.jsx
            ├── Dashboard.jsx
            ├── Gastos.jsx
            └── NovoGasto.jsx
```

---

## ⚙️ Como Rodar o Projeto

### Pré-requisitos

- Java 17+
- Node.js 18+
- MySQL 8+
- Maven

---

### 🗄️ Banco de Dados

Crie o banco no MySQL:

```sql
CREATE DATABASE gastos_db;
```

---

### 🚀 Backend

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/controle-gastos.git
cd controle-gastos/backend
```

2. Configure o `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gastos_db
spring.datasource.username=root
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

jwt.secret=${JWT_SECRET:sua-chave-secreta-com-mais-de-32-caracteres}
```

3. Rode o projeto:
```bash
mvn spring-boot:run
```

O backend estará disponível em `http://localhost:8080`

---

### 🎨 Frontend

1. Acesse a pasta do frontend:
```bash
cd controle-gastos/frontend
```

2. Instale as dependências:
```bash
npm install
```

3. Rode o projeto:
```bash
npm run dev
```

O frontend estará disponível em `http://localhost:5173`

---

## 🔌 Endpoints da API

### 🔓 Públicos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/cadastrar` | Cadastrar novo usuário |
| POST | `/auth/login` | Autenticar e obter token JWT |

### 🔒 Privados (requer `Authorization: Bearer <token>`)

**Gastos**

| Método | Rota | Descrição |
|---|---|---|
| GET | `/gastos` | Listar gastos paginados |
| POST | `/gastos` | Criar novo gasto |
| PUT | `/gastos/{id}` | Editar gasto |
| DELETE | `/gastos/{id}` | Deletar gasto |

**Filtros**

| Método | Rota | Descrição |
|---|---|---|
| GET | `/gastos/por-mes?mes=3&ano=2026` | Gastos por mês |
| GET | `/gastos/por-periodo?inicio=2026-01-01&fim=2026-03-31` | Gastos por período |
| GET | `/gastos/por-categoria?categoria=Alimentação` | Gastos por categoria |

**Estatísticas**

| Método | Rota | Descrição |
|---|---|---|
| GET | `/gastos/total-por-mes?mes=3&ano=2026` | Total do mês |
| GET | `/gastos/total-por-categoria` | Total por categoria |
| GET | `/gastos/media` | Média dos gastos |
| GET | `/gastos/maior` | Maior gasto |
| GET | `/gastos/menor` | Menor gasto |
| GET | `/gastos/alerta?limite=1000` | Alerta de limite |

**Exportação**

| Método | Rota | Descrição |
|---|---|---|
| GET | `/gastos/exportar-csv` | CSV completo |
| GET | `/gastos/exportar-csv-por-mes?mes=3&ano=2026` | CSV filtrado por mês |

---

## 🔐 Autenticação

O sistema usa **JWT (JSON Web Token)**. O fluxo é:

1. Cadastre um usuário em `POST /auth/cadastrar`
2. Faça login em `POST /auth/login` e receba o token
3. Use o token em todas as requisições no header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

O token expira em **1 hora**.

---

## 🖥️ Telas

| Tela | Descrição |
|---|---|
| Login / Cadastro | Autenticação com alternância entre formulários |
| Dashboard | Gráficos de pizza e barras, cards de estatísticas e alerta |
| Meus Gastos | Tabela paginada com editar, deletar e exportar CSV |
| Novo / Editar Gasto | Formulário com categorias, valor, descrição e data |

---

## 🌱 Variáveis de Ambiente

Para produção, configure as variáveis de ambiente:

```bash
# Windows
set JWT_SECRET=sua-chave-super-secreta-aqui
set DB_USERNAME=root
set DB_PASSWORD=sua_senha

# Linux/Mac
export JWT_SECRET=sua-chave-super-secreta-aqui
export DB_USERNAME=root
export DB_PASSWORD=sua_senha
```

---

## 👩‍💻 Autora

Feito por **Isabella** 🚀
