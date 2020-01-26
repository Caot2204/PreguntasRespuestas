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
package mx.pr.almacenamiento.entidadesjpa;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Carlos Onorio
 */
@Entity
@Table(name = "usuarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Lob
    @Column(name = "nombre")
    private byte[] nombre;
    @Basic(optional = false)
    @Lob
    @Column(name = "correo")
    private byte[] correo;
    @Basic(optional = false)
    @Lob
    @Column(name = "contrasenia")
    private byte[] contrasenia;
    @Lob
    @Column(name = "foto_perfil")
    private byte[] fotoPerfil;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creador")
    private Collection<Cuestionario> cuestionarioCollection;

    public Usuario() {
    }

    public Usuario(byte[] nombre) {
        this.nombre = nombre;
    }

    public Usuario(byte[] nombre, byte[] correo, byte[] contrasenia) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    public byte[] getNombre() {
        return nombre;
    }

    public void setNombre(byte[] nombre) {
        this.nombre = nombre;
    }

    public byte[] getCorreo() {
        return correo;
    }

    public void setCorreo(byte[] correo) {
        this.correo = correo;
    }

    public byte[] getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(byte[] contrasenia) {
        this.contrasenia = contrasenia;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    @XmlTransient
    public Collection<Cuestionario> getCuestionarioCollection() {
        return cuestionarioCollection;
    }

    public void setCuestionarioCollection(Collection<Cuestionario> cuestionarioCollection) {
        this.cuestionarioCollection = cuestionarioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombre != null ? nombre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.nombre == null && other.nombre != null) || (this.nombre != null && !this.nombre.equals(other.nombre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.pr.almacenamiento.entidadesjpa.Usuario[ nombre=" + nombre + " ]";
    }

}
