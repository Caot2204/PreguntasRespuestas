/*
 * Copyright (C) 2020 Carlos Onorio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mx.pr.almacenamiento.encriptacion;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Permite encriptar los datos recibidos y desencriptar los datos recuperados
 * del almacenamiento interno del juego Q&A
 *
 * @version 1.0 04 02 2020
 * @author David
 * (https://hashblogeando.wordpress.com/2019/09/13/encriptacion-aes-en-java/)
 */
public class Encriptador {

    private static Encriptador encriptador;
    private static String llaveAES;

    /**
     *
     * @param llaveAES Llave a utilizar para encriptar y desencritpar los datos
     * @return instancia del Encriptador
     */
    public static Encriptador obtenerInstancia(String llaveAES) {
        if (encriptador == null) {
            encriptador = new Encriptador();
        }
        setLlaveAES(llaveAES);
        return encriptador;
    }

    private static void setLlaveAES(String llaveAES) {
        Encriptador.llaveAES = llaveAES;
    }

    /**
     * Crea la clave de encriptación usada internamente
     *
     * @param clave Clave que se usara para encriptar
     * @return Clave de encriptación
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    private SecretKeySpec crearClave(String clave) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] claveEncriptacion = clave.getBytes("UTF-8");

        MessageDigest sha = MessageDigest.getInstance("SHA-1");

        claveEncriptacion = sha.digest(claveEncriptacion);
        claveEncriptacion = Arrays.copyOf(claveEncriptacion, 16);

        SecretKeySpec secretKey = new SecretKeySpec(claveEncriptacion, "AES");

        return secretKey;
    }

    /**
     * Aplica la encriptación AES a la cadena de texto usando la clave indicada
     *
     * @param datos Cadena a encriptar
     * @return Información encriptada
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encriptar(String datos) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec llaveInterna = this.crearClave(llaveAES);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, llaveInterna);

        byte[] datosEncriptar = datos.getBytes("UTF-8");
        byte[] bytesEncriptados = cipher.doFinal(datosEncriptar);
        String datosEncriptados = Base64.getEncoder().encodeToString(bytesEncriptados);

        return datosEncriptados;
    }

    /**
     * Desencripta la cadena de texto indicada usando la clave de encriptación
     *
     * @param datosEncriptados Datos encriptados
     * @return Informacion desencriptada
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String desencriptar(String datosEncriptados) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec llaveInterna = this.crearClave(llaveAES);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, llaveInterna);

        byte[] bytesEncriptados = Base64.getDecoder().decode(datosEncriptados);
        byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
        String datosOriginales = new String(datosDesencriptados);

        return datosOriginales;
    }

}
