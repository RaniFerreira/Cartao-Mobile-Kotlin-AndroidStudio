# 💳 MyCard — Simulador de Cartão de Crédito

Aplicativo Android nativo desenvolvido em **Kotlin**, com interface construída em **ConstraintLayout** e **Material Components**, que simula visualmente um cartão de crédito: preenchimento em tempo real, animação de giro 3D ao digitar o CVV, identificação automática de bandeira e validação dos dados antes da confirmação.

---

## 📖 Sobre o projeto

O **MyCard** é um simulador de preenchimento de cartão de crédito pensado para demonstrar boas práticas de desenvolvimento Android: uso de `ConstraintLayout` e `TextInputLayout` (Material) para uma interface responsiva e profissional, máscaras de texto em tempo real com `TextWatcher`, animação de rotação 3D com `ObjectAnimator`, e validação de formulário antes do envio.

A tela principal exibe um cartão interativo (frente e verso) que reflete o que o usuário digita no formulário abaixo, e um botão **Confirmar** que valida os dados antes de aceitar o envio.

---

## ✨ Funcionalidades

### 💳 Cartão interativo
Reproduz visualmente um cartão de crédito real, com chip, número mascarado, nome do titular, validade e bandeira — tudo atualizado em tempo real conforme o usuário digita.

### 🔢 Máscara do número do cartão
O campo `Número do cartão` formata automaticamente os dígitos em blocos de 4 (`•••• •••• •••• ••••`), tanto no campo de digitação quanto no cartão exibido.

### 📅 Máscara de validade
O campo `Validade` formata automaticamente para `MM/AA` conforme o usuário digita os números.

### 🔄 Giro 3D — *Desafio 1*
Ao focar no campo **CVV**, o cartão gira em 3D (`rotationY`) revelando o verso, onde o CVV digitado é exibido sobre a faixa magnética. Ao sair do campo CVV e focar em qualquer outro campo, o cartão gira de volta para a frente.

### 🏷️ Identificação de bandeira — *Desafio 2* 
Conforme os primeiros dígitos do número são digitados, o app identifica a bandeira automaticamente e atualiza o "logo" exibido no canto do cartão:
- Começa com **4** → `VISA`
- Começa com **5** → `MASTERCARD`
- Começa com **34** ou **37** → `AMEX`
- Sem dígitos suficientes → `CARTÃO`

### ✅ Validação final
Ao tocar em **Confirmar**, o app valida se o número do cartão tem 16 dígitos e se o nome do titular tem ao menos 3 caracteres. Se algum dado for inválido, o campo correspondente exibe um erro e recebe foco automaticamente; se tudo estiver correto, um `Toast` confirma que os dados são válidos.

---

## 🖼️ Capturas de tela

<div  align="center">
<table>
  <tr>
    <td align="center" colspan="2"><b>Cartão preenchido — bandeira identificada automaticamente</b></td>
  </tr>
  <tr>
    <td align="center"><img width="280" height="608" alt="Cartão com bandeira Visa" src="https://github.com/user-attachments/assets/bc9b083e-bcc2-48cf-84a8-43015e44d9ee" /></td>
    <td align="center"><img width="280" height="607" alt="Cartão com bandeira Mastercard" src="https://github.com/user-attachments/assets/8355e754-b031-467d-bfd2-74b4c28bde48" /></td>
  </tr>
  <tr>
    <td align="center" colspan="2"><b>Confirmação com dados válidos</b><br>Toast de sucesso ao tocar em Confirmar</td>
  </tr>
  <tr>
    <td align="center" colspan="2"><img width="280" height="610" alt="Confirmação com dados válidos" src="https://github.com/user-attachments/assets/4fb8c973-5c19-43a2-b69b-2add7e7a2e70" /></td>
  </tr>
  <tr>
    <td align="center" colspan="2"><b>Confirmação com dados inválidos</b><br>Erro exibido no campo e foco automático</td>
  </tr>
  <tr>
    <td align="center"><img width="278" height="612" alt="Erro no número do cartão" src="https://github.com/user-attachments/assets/1d07ed4d-4c34-4f16-90e0-160baf996fc3" /></td>
    <td align="center"><img width="286" height="607" alt="Erro no nome do titular" src="https://github.com/user-attachments/assets/4df91ad5-9de4-4d17-b569-28c4d8190376" /></td>
  </tr>
</table>

</div>


---
## 💻 Demosntração de giro ao clicar em cvv
<div align="center">
https://github.com/user-attachments/assets/d15d5967-c56b-40e3-a18f-20530a5d396a
</div>div>
## 🛠️ Tecnologias utilizadas

| Tecnologia | Uso no projeto |
|---|---|
| **Kotlin** | Linguagem principal da aplicação |
| **ConstraintLayout** | Construção da interface responsiva do cartão e formulário |
| **Material Components** | `TextInputLayout` e `MaterialButton` para os campos e botão |
| **AndroidX (AppCompat, CardView)** | Compatibilidade e exibição das duas faces do cartão |
| **ObjectAnimator** | Animação de giro 3D do cartão ao focar no CVV |
| **TextWatcher** | Máscaras em tempo real de número do cartão e validade |
| **Toast** | Feedback de validação dos dados |

---

## 💻 Código-fonte dos desafios

### Desafio 1 — Giro 3D do cartão ao focar no CVV

```kotlin
// Desafio 1: girar o cartão ao focar no CVV
private fun setupCardFlip() {
    etCvv.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) flipToBack() else flipToFront()
    }

    val backToFrontListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) flipToFront()
    }
    etCardNumber.onFocusChangeListener = backToFrontListener
    etHolderName.onFocusChangeListener = backToFrontListener
    etExpiry.onFocusChangeListener = backToFrontListener
}

// Anima a saída até 90°, troca a visibilidade e entra a outra face de -90° até 0°
private fun animateFlip(viewOut: View, viewIn: View) {
    val outAnim = ObjectAnimator.ofFloat(viewOut, "rotationY", 0f, 90f)
    outAnim.duration = 150
    outAnim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            viewOut.visibility = View.INVISIBLE

            viewIn.rotationY = -90f
            viewIn.visibility = View.VISIBLE
            val inAnim = ObjectAnimator.ofFloat(viewIn, "rotationY", -90f, 0f)
            inAnim.duration = 150
            inAnim.start()
        }
    })
    outAnim.start()
}
```

### Desafio 2 — Identificação automática de bandeira

```kotlin
// Identifica a bandeira pelos primeiros dígitos e atualiza o "logo" no cartão
private fun updateBrand(digits: String) {
    tvBrandLogo.text = when {
        digits.isEmpty() -> "•••"
        digits.startsWith("4") -> "VISA"
        digits.startsWith("5") -> "MASTERCARD"
        digits.startsWith("34") || digits.startsWith("37") -> "AMEX"
        else -> "CARTÃO"
    }
}
```

### Validação final antes do envio

```kotlin
private fun setupSubmit() {
    btnSubmit.setOnClickListener {
        val digits = etCardNumber.text.toString().filter { it.isDigit() }
        val name = etHolderName.text.toString().trim()
0
        when {
            digits.length != 16 -> {
                etCardNumber.error = "O número do cartão deve ter 16 dígitos"
                etCardNumber.requestFocus()
            }
            name.length < 3 -> {
                etHolderName.error = "O nome deve ter ao menos 3 caracteres"
                etHolderName.requestFocus()
            }
            else -> {
                Toast.makeText(this, "Dados válidos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

## 👤 Autor

Ranielly Ferreira — ADS, IFTM Campus Pa
