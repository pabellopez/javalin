// (()=>{
//     $(document).ready(()=>{
//
//     });
// })();

function login() {
    let params = {
        usuario: $("#user").val(),
        password: $("#password").val()
    }
    if (params.usuario === "") {
        alert("Debe digitar el usuario");
        return;
    }
    if (params.password === "") {
        alert("Debe digitar el password");
        return;
    }
    $.post(window.location + "/iniciar", params).then((data) => {
        if (data === "true") {
            window.location.reload();
        } else {
            alert("Usuario incorrecto");
        }
    }).fail((error) => {
        console.log(error)
    })
}