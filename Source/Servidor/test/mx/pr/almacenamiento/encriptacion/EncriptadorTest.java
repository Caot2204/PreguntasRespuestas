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
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Carlos Onorio
 */
public class EncriptadorTest {
    
    private Encriptador encriptador;

    @Test
    public void encriptarDesencriptarDatos() {
        encriptador = Encriptador.obtenerInstancia("30dst0045z");
        try {
            String datosOriginales = "Esta es la cadena original ";
            String datosEncriptados = encriptador.encriptar(datosOriginales);
            assertEquals("Prueba de encriptacion y desencriptacion", datosOriginales,
                         encriptador.desencriptar(datosEncriptados));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncriptadorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
