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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Onorio
 */
@Entity
@Table(name = "respuestas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Respuesta.findAll", query = "SELECT r FROM Respuesta r")
    , @NamedQuery(name = "Respuesta.findByCorrecta", query = "SELECT r FROM Respuesta r WHERE r.correcta = :correcta")})
public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RespuestaPK respuestaPK;
    @Lob
    @Column(name = "descripcion")
    private byte[] descripcion;
    @Lob
    @Column(name = "imagen")
    private byte[] imagen;
    @Basic(optional = false)
    @Column(name = "correcta")
    private boolean correcta;
    @JoinColumn(name = "pregunta", referencedColumnName = "numero", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Pregunta pregunta1;
    @JoinColumn(name = "cuestionario", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cuestionario cuestionario1;

    public Respuesta() {
    }

    public Respuesta(RespuestaPK respuestaPK) {
        this.respuestaPK = respuestaPK;
    }

    public Respuesta(RespuestaPK respuestaPK, boolean correcta) {
        this.respuestaPK = respuestaPK;
        this.correcta = correcta;
    }

    public Respuesta(byte[] letra, byte[] pregunta, byte[] cuestionario) {
        this.respuestaPK = new RespuestaPK(letra, pregunta, cuestionario);
    }

    public RespuestaPK getRespuestaPK() {
        return respuestaPK;
    }

    public void setRespuestaPK(RespuestaPK respuestaPK) {
        this.respuestaPK = respuestaPK;
    }

    public byte[] getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(byte[] descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public boolean getCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }

    public Pregunta getPregunta1() {
        return pregunta1;
    }

    public void setPregunta1(Pregunta pregunta1) {
        this.pregunta1 = pregunta1;
    }

    public Cuestionario getCuestionario1() {
        return cuestionario1;
    }

    public void setCuestionario1(Cuestionario cuestionario1) {
        this.cuestionario1 = cuestionario1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (respuestaPK != null ? respuestaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Respuesta)) {
            return false;
        }
        Respuesta other = (Respuesta) object;
        if ((this.respuestaPK == null && other.respuestaPK != null) || (this.respuestaPK != null && !this.respuestaPK.equals(other.respuestaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.pr.almacenamiento.entidadesjpa.Respuesta[ respuestaPK=" + respuestaPK + " ]";
    }
    
}
