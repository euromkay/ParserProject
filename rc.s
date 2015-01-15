/*
 * Generated Wed Jan 14 16:52:27 PST 2015
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
f:	.skip 20

! ---main---
	.section ".text"
	.align 4
	.global main
main:
	set			SAVE.main, %g1

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop

!5
	set			5, %l5
	mov			%fp, %l3
	set			4, %l4
	sub			%l3, %l4, %l4
	st			%l5, [%l4]

!float[5] f
	set			f, %l0
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
	set			.globalInit, %l0
	set			1, %l1
	st			%l1, [%l0]
.globalFinish:

!0
	set			0, %l0
	mov			%fp, %l2
	set			8, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!int i = 0
	mov			%fp, %l3
	set			8, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %l0
	st			%l0, [%l1]

!int i = 0

	set			0, %l0
	mov			%fp, %l2
	set			16, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]
.for0:
	set			5, %l0
	mov			%fp, %l2
	set			16, %l1
	sub			%l2, %l1, %l1
	ld			[%l1], %o1
	cmp			%o1, %l0
	be			.for0.DONE
	nop
	set			4, %o0
	call		.mul
	nop
	set			f, %l1
	ld			[%l1], %l0
	mov			%fp, %l2
	set			20, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]
!i P+ i
	mov			%fp, %l1
	set			12, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	mov			%fp, %l1
	set			24, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]
	inc			%o0
	mov			%fp, %l1
	set			12, %l0
	sub			%l1, %l0, %l0
	st			%o0, [%l0]

	mov			%fp, %l1
	set			20, %l0
	sub			%l1, %l0, %l0
	mov			%fp, %l3
	set			24, %l2
	sub			%l3, %l2, %l2
	ld			[%l2], %f0
	fitos		%f0, %f0
	mov			%fp, %l2
	set			20, %l1
	sub			%l2, %l1, %l1
	st			%f0, [%l1]
	st			%f0, [%l0]
!f = iP+
!f

.for0CHECK:
	mov			%fp, %l2
	set			16, %l1
	sub			%l2, %l1, %l1
	ld			[%l1], %l0
	inc			%l0
	st			%l0, [%fp]
	ba			.for0
	nop
.for0.DONE:

!foreach(float f : f)

	set			0, %l0
	mov			%fp, %l2
	set			36, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]
.for2:
	set			5, %l0
	mov			%fp, %l2
	set			36, %l1
	sub			%l2, %l1, %l1
	ld			[%l1], %o1
	cmp			%o1, %l0
	be			.for2.DONE
	nop
	set			4, %o0
	call		.mul
	nop
	set			f, %l1
	ld			[%l1], %l0
	ld			[%l0], %l0
	mov			%fp, %l2
	set			40, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]
	mov			%fp, %l1
	set			40, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %f0
	call		printFloat
	nop
	set			_strFmt, %o0
	set			_endl, %o1
	call		printf
	nop
!cout << f << endl

.for2CHECK:
	mov			%fp, %l2
	set			36, %l1
	sub			%l2, %l1, %l1
	ld			[%l1], %l0
	inc			%l0
	st			%l0, [%fp]
	ba			.for2
	nop
.for2.DONE:

!foreach(float f : f)

!1
	set			1, %l0
	mov			%fp, %l2
	set			48, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l1
	set			48, %l0
	sub			%l1, %l0, %l0
	ld			[%l0], %o0
	call		exit
	nop
!exit(1)

