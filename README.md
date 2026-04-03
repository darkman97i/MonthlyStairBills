# MonthlyStairBills

CLI application that generates PDF receipts for homeowner communities and sends them by email to each recipient.

## Requirements

- Java 17+
- Maven 3.8+
- A Gmail account with an [App Password](https://myaccount.google.com/apppasswords) configured

## Build & Run

Build and run in one step:

```bash
./run.sh
```

Or separately:

```bash
./build.sh
java -jar target/monthly-stair-bills-1.0.0.jar
```

> The application must be run from the directory where `bills.properties` is located.

## Output

- PDFs are generated in a `bills/` subdirectory (created automatically if it does not exist).
- Filename format: `{year}_{month}_{day}_{name}.pdf` — e.g. `2026_03_29_Josep_Llort.pdf`
- After each run the bill number in `bills.properties` is automatically incremented.

## bills.properties

This file must be placed in the directory where the application is executed.

```properties
# ==========================================
# Bill number (auto-incremented after each run)
# ==========================================
montly.stair.bill.number=700

# ==========================================
# Date
# ==========================================
montly.stair.bill.year=2026
montly.stair.bill.month=3
montly.stair.bill.day=29

# ==========================================
# Community
# ==========================================
montly.stair.bill.community.name=Comunitat propietaris c/Nord,99
montly.stair.bill.community.city=Alguno

# ==========================================
# Email (Gmail)
# ==========================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-account@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ==========================================
# Email templates
# Available placeholders: {name}, {number}, {year}, {concept}, {amount}, {community}
# Use \n for line breaks in the body
# ==========================================
spring.mail.subject=Comunicat {community}
spring.mail.body=Benvolgut/da {name},\n\nAdjuntem el rebut Nº {number} / {year} corresponent a:\n\n  Concepte : {concept}\n  Import   : {amount} €\n\n{community}\n

# ==========================================
# Recipients
# ==========================================
montly.stair.bill.recipient.1.active=true
montly.stair.bill.recipient.1.name=Josep Matas
montly.stair.bill.recipient.1.floor=1
montly.stair.bill.recipient.1.email=josep@email.com
montly.stair.bill.recipient.1.concept=Quota {month} escala
montly.stair.bill.recipient.1.quantity.in.euros=60

montly.stair.bill.recipient.2.active=false
montly.stair.bill.recipient.2.name=Maria Garcia
montly.stair.bill.recipient.2.floor=2
montly.stair.bill.recipient.2.email=maria@email.com
montly.stair.bill.recipient.2.concept=Quota {month} escala
montly.stair.bill.recipient.2.quantity.in.euros=60
```

### Property reference

| Property | Description |
|---|---|
| `montly.stair.bill.number` | Starting bill number. Auto-incremented after each run. |
| `montly.stair.bill.year` | Year printed on the receipt. |
| `montly.stair.bill.month` | Month (1–12). Used to resolve the `{month}` placeholder. |
| `montly.stair.bill.day` | Day printed on the receipt. |
| `montly.stair.bill.community.name` | Community name printed on the receipt and used in email templates. |
| `montly.stair.bill.community.city` | City printed on the receipt. |
| `spring.mail.username` | Gmail address used as sender. |
| `spring.mail.password` | Gmail App Password (not your regular password). |
| `spring.mail.subject` | Email subject template. |
| `spring.mail.body` | Email body template. Use `\n` for line breaks. |

### Recipient properties

Each recipient is identified by an index starting at 1. Add as many as needed.

| Property | Description |
|---|---|
| `...recipient.N.active` | `true` to include in the current run, `false` to skip. |
| `...recipient.N.name` | Full name. |
| `...recipient.N.floor` | Floor number (1–9). Rendered as Catalan ordinal: 1r, 2n, 3r, 4rt, 5è… |
| `...recipient.N.email` | Recipient email address. |
| `...recipient.N.concept` | Concept printed on the receipt. Supports `{month}` placeholder. |
| `...recipient.N.quantity.in.euros` | Amount in euros (integer). |

### Email template placeholders

| Placeholder | Replaced with |
|---|---|
| `{name}` | Recipient name |
| `{number}` | Bill number |
| `{year}` | Year |
| `{concept}` | Concept (after `{month}` substitution) |
| `{amount}` | Amount in euros |
| `{community}` | Community name |

## Gmail App Password setup

1. Go to **Manage your Google Account → Security**
2. Enable **2-Step Verification** (required)
3. Navigate directly to [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
4. Create a new App Password (name it e.g. `MonthlyStairBills`)
5. Copy the generated 16-character password into `spring.mail.password` (no spaces)
