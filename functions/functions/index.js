const functions = require("firebase-functions");

const admin = require('firebase-admin');
admin.initializeApp();

exports.yeniMesajBildirimi=functions.database.ref("/mesajlar/{mesaj_gonderilen_user}/{mesaj_gonderen_user}/{yeni_mesaj}").onCreate((data,context)=>{
  const mesajGonderilenUser=context.params.mesaj_gonderilen_user;
  const mesajGonderenUser=context.params.mesaj_gonderen_user;
  const yeniMesaj=context.params.yeni_mesaj;

  console.log("mesaj gonderilen:",mesajGonderilenUser);
  console.log("mesaj gonderen:",mesajGonderenUser);
  console.log("mesaj:",yeniMesaj);



    const mesajGonderilenUserToken = admin.database().ref(`/users/isletmeler/${mesajGonderilenUser}/FCM_TOKEN`).once('value');




    const mesajGonderenUserName=admin.database().ref(`/users/isletmeler/${mesajGonderenUser}/user_name`).once('value');

    console.log("mesajGonderilenUserToken:",mesajGonderilenUserToken);
    console.log("mesajGonderenUserName:",mesajGonderenUserName);







  const gonderilenMesaj=admin.database().ref(`/mesajlar/${mesajGonderilenUser}/${mesajGonderenUser}/${yeniMesaj}`).once('value');

  return mesajGonderilenUserToken.then(result=>{

    const user_token=result.val();

    console.log("mesajGonderilenUserToken:",mesajGonderilenUserToken);


    return mesajGonderenUserName.then(result=>{

      const user_name=result.val();

      console.log("mesajGonderenUserName:",mesajGonderenUserName);


      return gonderilenMesaj.then(result=>{


      const son_yazilan_mesaj=result.child('mesaj').val();

      const mesaj_gonderen_user_id=result.child('user_id').val();


      if(mesaj_gonderen_user_id==mesajGonderenUser){

        const yeniMesajBildirimi={

          notification:{


                    title:'Yeni Mesaj',
                    body: `${user_name}:${son_yazilan_mesaj}`,
                    icon:'default'

          },

          data:{
            konusulacakKisi:`${mesajGonderenUser}`
          }
          };

        return admin.messaging().sendToDevice(user_token,yeniMesajBildirimi).then(result=>{


          console.log("yeni mesaj");

          });
          }

         });


    });



  });



});
