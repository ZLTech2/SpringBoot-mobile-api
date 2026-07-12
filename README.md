# Negócio na Área - API

O Negócio na Área é uma plataforma desenvolvida para aproximar pequenas empresas de clientes, oferecendo ferramentas para divulgação de produtos, localização de estabelecimentos, promoções, interação entre usuários e acompanhamento de métricas por meio de dashboards e relatórios.

API REST desenvolvida em **Spring Boot** para a plataforma **Negócio na Área**, responsável pelo gerenciamento de usuários, empresas, produtos, autenticação, promoções, notificações e relatórios.

## 🚀 Funcionalidades

- Autenticação com JWT
- Cadastro de empresas
- Cadastro de clientes
- Cadastro de produtos
- Promoções
- Dashboard
- Relatórios em PDF
- Upload de imagens
- Integração com IA (OpenAI)
- Notificações por e-mail
- Geolocalização

## 🛠️ Tecnologias

- Java
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Hibernate
- Cloudinary
- OpenAI API
- Resend
- Maven

## 🌐 API

A API está hospedada no **Render** e utiliza variáveis de ambiente configuradas no ambiente de produção.

Caso deseje executar o projeto localmente, configure as seguintes variáveis de ambiente:

```env
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
OPENAI_API_KEY=
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
RESEND_API_KEY=
```

## ▶️ Executando localmente

```bash
git clone https://github.com/ZLTech2/Negocio_Area_API.git

cd Negocio_Area_API

./mvnw spring-boot:run
```

## 🏗️ Arquitetura

```
React Native          Frontend Web
      │                     │
      └──────────┬──────────┘
                 │
                 ▼
          Spring Boot API
                 │
                 ▼
            PostgreSQL
                 │
     ┌───────────┼────────────┐
     │           │            │
 Cloudinary   OpenAI      Resend
```

## 🔗 Repositórios

- 📱 Mobile: https://github.com/ZLTech2/Negocio_Area_Mobile
- 💻 Web: https://github.com/ZLTech2/negocioAreaWeb

## 🌍 Aplicação Web

https://negocio-area-web.vercel.app/

## 📄 Licença

Projeto desenvolvido como Trabalho de Graduação da Fatec.
