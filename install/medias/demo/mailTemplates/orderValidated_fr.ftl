<div style="width:100%;text-align:center">
    <a href="https://apps-jeeshop.rhcloud.com">
        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">
    </a>
</div>

<h3>Bonjour ${user.gender} ${user.firstname} ${user.lastname},</h3>

<p>Nous avons le plaisir de vous confirmer la validation de votre commande numéro <strong>${reference}</strong></p>

<p>Vous recevrez prochainement un e-mail lors de son expédition par nos services.</p>

<h3><em>Détail : </em></h3>

<table style="width:100%;border-width:1px; border-style=solid; text-align:justify">
    <thead>
    <th></th>
    <th>Article</th>
    <th>Quantité</th>
    <th>Prix</th>
    </thead>
    <tbody>
    <#list items as item>
    <tr>
        <td style="text-align:center">
            <img src="https://apps-jeeshop.rhcloud.com/jeeshop-media/${item.presentationImageURI}" style="width:4em;height:auto"></img>
        </td>
        <td>${item.displayName}</td>
        <td>${item.quantity}</td>
        <td>${item.price} € TTC</td>
    </tr>
    </#list>
    <tr>
        <td style="text-align:center">
        </td>
        <td>Livraison par transporteur</td>
        <td></td>
        <td>${deliveryFee} € TTC</td>
    </tr>
    <#list orderDiscounts as orderDiscount>
    <tr>
        <td style="text-align:center">
        </td>
        <td>${orderDiscount.displayName}</td>
        <td></td>
        <td>- ${orderDiscount.discountValue} <#if orderDiscount.rateType>%<#else>€ TTC</#if></td>
    </tr>
    </#list>
    <tr>
        <td></td>
        <td></td>
        <td><i>Total HT</i></td>
        <td><i>#{price*100/(100+vat); m2M2} €</i></td>
    </tr>
    <tr>
        <td></td>
        <td></td>
        <td><b><i>Total TTC</i><b></td>
        <td><b><i>${price} €</i></b></td>
    </tr>
    </tbody>
</table>
<br/>

<table style="width:100%">
    <tr>
        <td style="width:45%">
            <h4>Adresse de livraison</h4>
            <p>
            ${deliveryAddress.gender}
  ${deliveryAddress.firstname}
  ${deliveryAddress.lastname}
            </p>

            <p>
                <b><i>Société</i></b>
                <br/>
            ${deliveryAddress.company!''}
            </p>

            <p>
                <b><i>Adresse</i></b>
                <br/>
            ${deliveryAddress.street}
                <br/>
            ${deliveryAddress.city}
                <br/>
            ${deliveryAddress.zipCode}
            </p>

            <p>
                <b><i>Pays</i></b>
                <br/>
            ${deliveryAddress.countryIso3Code}
            </p>
        </td>
        <td style="width:10%"></td>
        <td style="width:45%">
            <h4>Adresse de facturation</h4>
            <p>
            ${billingAddress.gender}
  ${billingAddress.firstname}
  ${billingAddress.lastname}
            </p>

            <p>
                <b><i>Société</i></b>
                <br/>
            ${billingAddress.company!''}
            </p>

            <p>
                <b><i>Adresse</i></b>
                <br/>
            ${billingAddress.street}
                <br/>
            ${billingAddress.city}
                <br/>
            ${billingAddress.zipCode}
            </p>

            <p>
                <b><i>Pays</i></b>
                <br/>
            ${billingAddress.countryIso3Code}
            </p>
        </td>
    </tr>
</table>

<p>Cordialement</p>