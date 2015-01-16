/*
 * Generated Fri Jan 16 00:09:18 PST 2015
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

! ---main---
	.section ".text"
	.align 4
	.global main
main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l2
	ld			[%l2], %l2
	cmp			%l2, %g0
	bne			.globalFinish
	nop

	set			.globalInit, %l2
	set			1, %l3
	st			%l3, [%l2]
.globalFinish:

!1
	set			1, %l0
	mov			%fp, %l2
	set			4, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!2.2
	.section ".rodata"
	.align 4
_float_2.20:	.single 0r2.2

	.section ".text"
	.align 4
	set			_float_2.20, %l0
	ld			[%l0], %l0
	mov			%fp, %l2
	set			8, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!StructType@6b125fac c4 : (1, 2.2)
!1
	set			1, %l0
	mov			%fp, %l2
	set			16, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!5
	set			5, %l0
	mov			%fp, %l2
	set			20, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!null Illegal call to overloaded function MYS_C. : (1, 5)
!1
	set			1, %l0
	mov			%fp, %l2
	set			24, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!2
	set			2, %l0
	mov			%fp, %l2
	set			28, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!3
	set			3, %l0
	mov			%fp, %l2
	set			32, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

!null Illegal call to overloaded function MYS_C. : (1, 2, 3)
	ret
	restore



	SAVE.main = -(92 + 32) & -8
