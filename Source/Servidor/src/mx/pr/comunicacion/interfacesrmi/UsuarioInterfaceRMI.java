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
package mx.pr.comunicacion.interfacesrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import mx.pr.comunicacion.objetoscliente.UsuarioCliente;

/**
 * Provee al cliente los métodos disponibles a usar relacionados con las cuentas
 * de usuario dentro del servidor del juego
 *
 * @version 1.0 29 01 2020
 * @author Carlos Onorio
 */
public interface UsuarioInterfaceRMI extends Remote {

    /**
     * Almacena los datos de una cuenta de usuario en el servidor del juego.
     *
     * @param usuario Objeto con los datos del nuevo usuario, recibido desde un
     * cliente
     * @return true si se guardó exitosamente, false si no fué así
     * @throws RemoteException lanzada si ocurre algún problema en la conexión
     * cliente-servidor
     */
    boolean registrarUsuario(UsuarioCliente usuario) throws RemoteException;

    /**
     * Modifica los datos de un usuario registrado en el servidor del juego
     *
     * @param usuario Objeto con los datos actualizados de un usuario, recibido
     * desde el cliente
     * @return true si se actualizó correctamente, false si no fue así
     * @throws RemoteException lanzada si ocurre algún problema en la conexión
     * cliente-servidor
     */
    boolean actualizarUsuario(UsuarioCliente usuario) throws RemoteException;

    /**
     * Crea una nueva sesíon de un usuario en el servidor, si el correo y
     * contraseña son correctos
     *
     * @param correo Correo electrónico de la cuenta de usuario
     * @param contrasenia Contraseña de la cuenta de usuario
     * @return true si se inició correctamente la sesión, false si los datos son
     * incorrectos
     * @throws RemoteException lanzada si ocurre algún problema en la conexión
     * cliente-servidor
     */
    boolean iniciarSesion(String correo, String contrasenia) throws RemoteException;

    /**
     * Elimina del servidor la sesión de usuario que coincida con el correo
     * recibido del cliente
     *
     * @param correo Correo electrónico de la cuenta de usuario
     * @return true si se cerró la sesión, false si ocurrió algún problema
     * @throws RemoteException lanzada si ocurre algún problema en la conexión
     * cliente-servidor
     */
    boolean cerrarSesion(String correo) throws RemoteException;

}
