const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notificarNuevoMensaje = functions.database
  .ref("/Chats/{chatId}/{mensajeId}")
  .onCreate(async (snapshot, context) => {

    const mensaje = snapshot.val();

    const emisorUid = mensaje.emisorUid;
    const receptorUid = mensaje.receptorUid;
    const texto = mensaje.tipoMensaje === "IMAGEN"
      ? "📷 Se envió una imagen"
      : mensaje.mensaje;

    // Obtener token del receptor
    const tokenSnapshot = await admin
      .database()
      .ref(`/Usuarios/${receptorUid}/token`)
      .once("value");

    const token = tokenSnapshot.val();

    if (!token) {
      console.log("Usuario sin token");
      return null;
    }

    const payload = {
      notification: {
        title: "Nuevo mensaje",
        body: texto
      },
      data: {
        emisorUid: emisorUid
      }
    };

    return admin.messaging().sendToDevice(token, payload);
  });
