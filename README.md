Sistema de Gerenciamento de Restaurante

Um sistema completo para gerenciamento de reservas e pedidos em restaurantes, desenvolvido em Java com interface Swing e banco de dados SQLite.

📋 Funcionalidades
🛎️ Sistema de Reservas
Cadastro de clientes (Regulares e VIP)

Reserva de mesas com data/hora específica

Visualização de mesas disponíveis

Cancelamento de reservas

Validação de conflitos de horários

🍽️ Sistema de Pedidos
Cadastro de produtos no cardápio

Registro de pedidos por mesa

Controle de status dos pedidos

Visualização de pedidos em andamento

🛠️ Tecnologias Utilizadas
Linguagem: Java 17+

Interface Gráfica: Java Swing

Banco de Dados: SQLite (embutido)

Gerenciamento de Dependências: Maven

Padrões de Projeto: MVC, Singleton, DAO

📦 Estrutura do Projeto
text
src/
├── model/          # Classes de modelo e acesso a dados
│   ├── Cliente.java
│   ├── Mesa.java
│   ├── Reserva.java
│   ├── Produto.java
│   ├── Pedido.java
│   └── Database.java
├── view/           # Interfaces gráficas
│   ├── MainFrame.java
│   ├── ReservaPanel.java
│   ├── ListarReservasPanel.java
│   └── ...
└── controller/     # Lógica de controle
    ├── ReservaController.java
    └── PedidoController.java
🚀 Como Executar
Pré-requisitos
JDK 17 ou superior

Maven (para gerenciamento de dependências)

Instalação
Clone o repositório:

bash
git clone https://github.com/JoaoGnoattozin/sistema-restaurante-java.git
cd sistema-restaurante-java
Compile o projeto:

bash
mvn clean install
Execute a aplicação:

bash
java -jar target/restaurante.jar
Configuração
O banco de dados SQLite será criado automaticamente na primeira execução no arquivo restaurante.db.

✉️ Contato
João Gnoatto - jpgnoatto15@gmail.com

Projeto no GitHub: https://github.com/JoaoGnoattozin/sistema-restaurante-java

📌 Funcionalidades Detalhadas
Cadastro de Clientes
Registro de clientes comuns e VIP

Validação de telefone (11 dígitos)

Descontos especiais para clientes VIP

Gestão de Mesas
10 mesas disponíveis (8 regulares e 2 VIP)

Capacidade variável (4-6 pessoas)

Status de ocupação em tempo real

Sistema de Reservas
Agendamento por data e hora

Prevenção de conflitos de reservas

Visualização em calendário

Cardápio Digital
Cadastro de produtos com foto

Categorização por tipo (entrada, prato principal, sobremesa)

Atualização de preços

Controle de Pedidos
Vinculação de pedidos às mesas

Acompanhamento do status (em preparo, pronto, entregue)

Fechamento de conta com opção de desconto
