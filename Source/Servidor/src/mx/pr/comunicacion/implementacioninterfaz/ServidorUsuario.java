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
package mx.pr.comunicacion.implementacioninterfaz;

import java.rmi.RemoteException;
import mx.pr.almacenamiento.encriptacion.Encriptador;
import mx.pr.comunicacion.interfacesrmi.UsuarioInterfaceRMI;
import mx.pr.comunicacion.objetoscliente.UsuarioCliente;

/**
 * Permite a los usuarios almacenar y actualizar su información dentro del
 * sistema, así como iniciar sesión en él
 *
 * @version 1.0 01 02 2020
 * @author Carlos Onorio
 */
public class ServidorUsuario implements UsuarioInterfaceRMI {
    
    private final String CLAVE_AES_USUARIO = "gaop";
    private final Encriptador encriptador;
    private ServidorUsuario servidor;
    
    /**
     * 
     * @return instancia del servidor para los usuarios
     */
    public ServidorUsuario obtenerInstancia() {
        if (servidor == null) {
            this.servidor = new ServidorUsuario();
        }
        return servidor;
    }
    
    private ServidorUsuario() {
        this.encriptador = Encriptador.obtenerInstancia(CLAVE_AES_USUARIO);
    }

    @Override
    public boolean registrarUsuario(UsuarioCliente usuario) throws RemoteException {
        boolean guardadoCorrecto = false;
        
        
        
        return guardadoCorrecto;
    }

    @Override
    public boolean actualizarUsuario(UsuarioCliente usuario) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean iniciarSesion(String correo, String contrasenia) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cerrarSesion(String correo) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
