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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 *
 * @author Carlos Onorio
 */
@Embeddable
public class PreguntaPK implements Serializable {

    @Basic(optional = false)
    @Lob
    @Column(name = "numero")
    private byte[] numero;
    @Basic(optional = false)
    @Lob
    @Column(name = "cuestionario")
    private byte[] cuestionario;

    public PreguntaPK() {
    }

    public PreguntaPK(byte[] numero, byte[] cuestionario) {
        this.numero = numero;
        this.cuestionario = cuestionario;
    }

    public byte[] getNumero() {
        return numero;
    }

    public void setNumero(byte[] numero) {
        this.numero = numero;
    }

    public byte[] getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(byte[] cuestionario) {
        this.cuestionario = cuestionario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numero != null ? numero.hashCode() : 0);
        hash += (cuestionario != null ? cuestionario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PreguntaPK)) {
            return false;
        }
        PreguntaPK other = (PreguntaPK) object;
        if ((this.numero == null && other.numero != null) || (this.numero != null && !this.numero.equals(other.numero))) {
            return false;
        }
        if ((this.cuestionario == null && other.cuestionario != null) || (this.cuestionario != null && !this.cuestionario.equals(other.cuestionario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.pr.almacenamiento.entidadesjpa.PreguntaPK[ numero=" + numero + ", cuestionario=" + cuestionario + " ]";
    }
    
}
