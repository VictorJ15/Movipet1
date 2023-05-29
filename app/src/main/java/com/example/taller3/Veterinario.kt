package com.example.taller3

import android.os.Parcel
import android.os.Parcelable

data class Veterinario (
    var nombre: String? = null,
    var apellido: String? = null,
    var correo : String? = null,
    var password: String? = null,
    var placa : String? = null,
    var foto: String? = null,
    var estado: String? = null,
    var latitud: String? = null,
    var longitud: String? = null,
    var uid : String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foto)
        parcel.writeString(nombre)
        parcel.writeString(apellido)
        parcel.writeString(correo)
        parcel.writeString(password)
        parcel.writeString(placa)
        parcel.writeString(estado)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Veterinario> {
        override fun createFromParcel(parcel: Parcel): Veterinario {
            return Veterinario(parcel)
        }

        override fun newArray(size: Int): Array<Veterinario?> {
            return arrayOfNulls(size)
        }
    }
}
