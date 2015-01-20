/*
 * Generated Mon Jan 19 17:37:38 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

	.section ".data"
	.align 4
r:	.single 0r0

	.section ".data"
	.align 4
ai:	.skip 40

	.section ".data"
	.align 4
ar:	.skip 40

	.section ".data"
	.align 4
i:	.word 0

	.section ".data"
	.align 4
ci:	.word 0
! ---fi---
	.section ".text"
	.align 4
	.global fi
fi:
	set			SAVE.fi, %g1
	save		%sp, %g1, %sp


!int x

	mov			%fp, %l1
	set			4, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.fi = -(92 + 4) & -8
! ---main---
	.section ".text"
	.align 4
	.global main
main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop

!float r
!10
	set			10, %l7
	mov			%fp, %l6
	set			4, %l5
	sub			%l6, %l5, %l5
	st			%l7, [%l5]

!int[10] ai
	set			ai, %l0
	set			4, %l1
	set			0, %l2
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
!10
	set			10, %l0
	mov			%fp, %l2
	set			8, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!float[10] ar
	set			ar, %l0
	set			4, %l1
	set			0, %l2
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
	st			%l2, [%l0]
	add			%l0, %l1, %l0
!int i
!42
	set			42, %l0
	mov			%fp, %l2
	set			12, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!const int ci = 42
	mov			%fp, %l3
	set			12, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	st			%l0, [%l1]

	set			.globalInit, %l0
	set			1, %l1
	st			%l1, [%l0]
.globalFinish:

	set			i, %l2
	ld			[%l2], %l0
	cmp			%l0, %g0
	bl			arraybad0
	nop
	set			10, %l1
	cmp			%l0, %l1
	bl			arraygood0
	nop
arraybad0:
	set			arrrray_error, %o0
	set			i, %l2
	ld			[%l2], %o0
	set			10, %o2
	call		printf
	nop
	set			1, %o0
	call		exit
	nop
arraygood0:
!r = 
	set			r, %l0
	mov			%fp, %l3
	set			16, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %f0
	fitos			%f0, %f0
	set			r, %l1
	st			%f0, [%l1]

!0
	set			0, %l0
	mov			%fp, %l2
	set			20, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!r = Type of expression referenced by array subscript (int) is not of array or pointer type.
!0
	set			0, %l0
	mov			%fp, %l2
	set			24, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!r = Type of expression referenced by array subscript (funcptr : int ()) is not of array or pointer type.
!r = Type of expression referenced by array subscript (float) is not of array or pointer type.
!2.0
	.section ".rodata"
	.align 4
_float_2.01:	.single 0r2.0

	.section ".text"
	.align 4
	set			_float_2.01, %l0
	ld			[%l0], %l0
	mov			%fp, %l2
	set			28, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!r = Type of index expression in array reference (float) not equivalent to int.
!1
	set			1, %l0
	mov			%fp, %l2
	set			32, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!1
	set			1, %l0
	mov			%fp, %l2
	set			36, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!1
	set			1, %l0
	mov			%fp, %l2
	set			40, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!0
	set			0, %l0
	mov			%fp, %l2
	set			44, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l3
	set			44, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	cmp			%l0, %g0
	bl			arraybad2
	nop
	set			10, %l1
	cmp			%l0, %l1
	bl			arraygood2
	nop
arraybad2:
	set			arrrray_error, %o0
	mov			%fp, %l3
	set			44, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %o0
	set			10, %o2
	call		printf
	nop
	set			1, %o0
	call		exit
	nop
arraygood2:
!1 + 
	mov			%fp, %l1
	set			40, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			48, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	add			%o0, %o1, %o0
	mov			%fp, %l1
	set			52, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

!1
	set			1, %l0
	mov			%fp, %l2
	set			56, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!1 +  + 1
	mov			%fp, %l1
	set			52, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			56, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	add			%o0, %o1, %o0
	mov			%fp, %l1
	set			60, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

	mov			%fp, %l3
	set			60, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	cmp			%l0, %g0
	bl			arraybad3
	nop
	set			10, %l1
	cmp			%l0, %l1
	bl			arraygood3
	nop
arraybad3:
	set			arrrray_error, %o0
	mov			%fp, %l3
	set			60, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %o0
	set			10, %o2
	call		printf
	nop
	set			1, %o0
	call		exit
	nop
arraygood3:
!1 + 
	mov			%fp, %l1
	set			36, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			64, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	add			%o0, %o1, %o0
	mov			%fp, %l1
	set			68, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

	mov			%fp, %l3
	set			68, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	cmp			%l0, %g0
	bl			arraybad4
	nop
	set			10, %l1
	cmp			%l0, %l1
	bl			arraygood4
	nop
arraybad4:
	set			arrrray_error, %o0
	mov			%fp, %l3
	set			68, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %o0
	set			10, %o2
	call		printf
	nop
	set			1, %o0
	call		exit
	nop
arraygood4:
!1 + 
	mov			%fp, %l1
	set			32, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			72, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o1
	add			%o0, %o1, %o0
	mov			%fp, %l1
	set			76, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

	mov			%fp, %l3
	set			76, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	cmp			%l0, %g0
	bl			arraybad5
	nop
	set			10, %l1
	cmp			%l0, %l1
	bl			arraygood5
	nop
arraybad5:
	set			arrrray_error, %o0
	mov			%fp, %l3
	set			76, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %o0
	set			10, %o2
	call		printf
	nop
	set			1, %o0
	call		exit
	nop
arraygood5:
!r = 
	set			r, %l0
	mov			%fp, %l3
	set			80, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l1
	st			%l1, [%l0]

!0
	set			0, %l0
	mov			%fp, %l2
	set			84, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l1
	set			84, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %i0
	ret
	restore


	ret
	restore



	SAVE.main = -(92 + 84) & -8
