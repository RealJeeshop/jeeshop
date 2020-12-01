<div style="width:100%;text-align:center">
    <a href="https://apps-jeeshop.rhcloud.com">
        <img src="https://apps-jeeshop.rhcloud.com/jeeshop-store/images/logo.png" style=" width: 10em; height: auto; padding-bottom: 1em;" alt="Jeeshop store demo">
    </a>
</div>

<h3>Hello ${user.gender} ${user.firstname} ${user.lastname},</h3>

<p>We are glad to confim the validation of your order <strong>${reference}</strong></p>

<p>You will receive another e-mail when it will be shipped.</p>

<h3><em>Details :</em></h3>

<table style="width:100%;border-width:1px; border-style=solid; text-align:justify">
    <thead>
    <th></th>
    <th>Item</th>
    <th>Quantity</th>
    <th>Price</th>
    </thead>
    <tbody>
    <#list items as item>
    <tr>
        <td style="text-align:center">
            <img src="https://apps-jeeshop.rhcloud.com/jeeshop-media/${item.presentationImageURI}" style="width:4em;height:auto"></img>
        </td>
        <td>${item.displayName}</td>
        <td>${item.quantity}</td>
        <td>${item.price} €</td>
    </tr>
    </#list>
    <tr>
        <td style="text-align:center">
        </td>
        <td>Package delivery company</td>
        <td></td>
        <td>${deliveryFee} €</td>
    </tr>
    <#list orderDiscounts as orderDiscount>
    <tr>
        <td style="text-align:center">
        </td>
        <td>${orderDiscount.displayName}</td>
        <td></td>
        <td>- ${orderDiscount.discountValue} <#if orderDiscount.rateType>%<#else>€</#if></td>
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
            <h4>Delivery address</h4>
            <p>
            ${deliveryAddress.gender}
  ${deliveryAddress.firstname}
  ${deliveryAddress.lastname}
            </p>

            <p>
                <b><i>Company</i></b>
                <br/>
            ${deliveryAddress.company!''}
            </p>

            <p>
                <b><i>Address</i></b>
                <br/>
            ${deliveryAddress.street}
                <br/>
            ${deliveryAddress.city}
                <br/>
            ${deliveryAddress.zipCode}
            </p>

            <p>
                <b><i>Country</i></b>
                <br/>
            ${deliveryAddress.countryIso3Code}
            </p>
        </td>
        <td style="width:10%"></td>
        <td style="width:45%">
            <h4>Billing address</h4>
            <p>
            ${billingAddress.gender}
  ${billingAddress.firstname}
  ${billingAddress.lastname}
            </p>

            <p>
                <b><i>Company</i></b>
                <br/>
            ${billingAddress.company!''}
            </p>

            <p>
                <b><i>Address</i></b>
                <br/>
            ${billingAddress.street}
                <br/>
            ${billingAddress.city}
                <br/>
            ${billingAddress.zipCode}
            </p>

            <p>
                <b><i>Country</i></b>
                <br/>
            ${billingAddress.countryIso3Code}
            </p>
        </td>
    </tr>
</table>

<p>Best regards</p>